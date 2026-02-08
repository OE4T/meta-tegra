Notes on extending support for flashing Jetson devices that boot from external storage media (NVMe, USB).

Last update: 25 Jul 2025

This is currently supported on branches based off JetPack 5/L4T R35 or later, and `kirkstone-l4t-r32.7.x`.  For R32.7.x, there is support for T210 (TX1/Nano) as well as T186 (TX2) and T194 (Xavier) targets.

# Prerequisites

Beyond the normal host tools required for building and normal flashing, you should also have these commands available on your build host:
* `sgdisk` (from the `gdisk`/`gptfdisk` package)
* `udisksctl` (part of the `udisks2` package)

You should disable automatic mounting of removable media in your desktop settings. On recent Ubuntu (GNOME), go to Settings -> Removable Media, and check the box next to "Never prompt or start programs on media insertion."  You may also need to update the `/org/gnome/desktop/media-handling/automount` setting via `dconf`.  Check the setting with:
```
$ dconf read /org/gnome/desktop/media-handling/automount
```
If it reports `true`, set it with:
```
$ dconf write /org/gnome/desktop/media-handling/automount false
```

For Ubuntu 24.04, use `gsettings`, and also disable `automount-open`:
```
$ gsettings set org.gnome.desktop.media-handling automount false
$ gsettings set org.gnome.desktop.media-handling automount-open false
```

If the `bmaptool` command is available, it will be used for writing to the storage device, which speeds up writes but (currently) requires root privileges (the scripts will automatically use `sudo` to invoke it when needed).

No additional host changes should be required

Your image needs to include a device-tree with `usb2-0` in `otg` mode - as [here](https://github.com/OE4T/linux-tegra-4.9/blob/oe4t-patches-l4t-r32.7.4/nvidia/platform/t19x/jakku/kernel-dts/common/tegra194-p3668-common.dtsi#L127).

## Avoiding Sudo

Note: `sudo` access will be needed when writing the disks using `bmap-tools`. This method below will avoid sudo while mounting/unmounting the flaskpkg and related block devices.

For running the `initrd-flash` script without `sudo`, the host changes mentioned in the "Avoiding sudo" section on the [Flashing the Jetson Dev Kit](https://github.com/OE4T/meta-tegra/wiki/Flashing-the-Jetson-Dev-Kit) wiki page still apply.

In addition, to avoid prompts for authentication at several points in the process you need to configure polkit appropriately.  On Ubuntu 22.04 this can be accomplished with the following script snippet run as sudo root:
```
cat << EOF > /var/lib/polkit-1/localauthority/50-local.d/com.github.oe4t.pkla
[Allow Mounting for Disk Group]
Identity=unix-group:disk
Action=org.freedesktop.udisks2.filesystem-mount
ResultAny=yes

[Allow Power Off Drive for Disk Group]
Identity=unix-group:disk
Action=org.freedesktop.udisks2.power-off-drive
ResultAny=yes
EOF
chmod 644 /var/lib/polkit-1/localauthority/50-local.d/com.github.oe4t.pkla
systemctl restart polkit
```

# Build configuration

No configuration is required if you just want to use initrd flashing and still keep your rootfs on the Jetson's internal storage device. You only need to add a configuration setting if you want to configure your system to have its rootfs (`APP` partition) on an external storage device.  To do that, add a line to your `local.conf` such as:
```
TNSPEC_BOOTDEV:jetson-xavier-nx-devkit-emmc = "nvme0n1p1"
```

* If trying this out with a different Jetson device, use the MACHINE name for the override in the above.
* If trying USB storage instead of NVMe, use `sda1` as the boot device, instead of `nvme0n1p1`.

# Flashing after build

1. Put the Jetson device into recovery mode and connect it to your host via the USB OTG port.
2. Unpack the `tegraflash` tarball into an empty directory.
3. `cd` to that directory and run `./initrd-flash` to start the flashing process.

The script:
1. Uses the RCM boot feature to download a special [initrd and kernel](https://github.com/OE4T/meta-tegra/blob/master/recipes-core/images/tegra-initrd-flash-initramfs.bb) that sets up the device as a USB mass storage gadget.
2. Waits for the USB storage device to appear on the host, then copies in the bootloader files and a command sequence for the target that instructs it to start the boot device update, and tells it which storage device(s) should be exported to the host for writing.
3. Uses the `make-sdcard` script to write to storage device(s).  This happens in parallel with the target's programming of the boot device.
4. Waits for the target to export another storage device to report its final status and the logs generated on the target. The script copies the device logs into a subdirectory.  When finished, it releases the storage device, and the target reboots automatically.

**Note:** add the current Linux user to the `disk` group to avoid the usage of `sudo` to run `initrd-flash` script.


# Re-flashing just the rootfs storage device
The `initrd-flash` script has a `--skip-bootloader` option for skipping the programming of the boot partitions, so you can re-flash just the rootfs storage device.  You should only use this option if you have already programmed the boot partitions once with the versions you're using for your current build.

# Possible future enhancements
* Develop the kernel/initrd used here into a more general "recovery" image, and/or applied for cross-version OTA updates, although the specific use cases will probably require something a bit different and need more customization.
* See if something could be done to automate setup when using LUKS encryption.  Direct formatting and partition writing from the host isn't really an option there.  A hybrid approach (formatting and cryptsetup done on the device, then exporting the encrypted partitions via USB) should be workable.

# How it works

* The helper scripts now support an `--external-device` option that passes appropriate options to `tegraflash.py` (needed since one of the BCTs appears to include information the external storage device for the boot chain to work), and an `--rcm-boot` option to allow direct download/execution of a kernel+initrd image.
* The SDcard-related support in the `nvflashxmlparse` and `make-sdcard` scripts was generalized to distinguish between the 'boot' device and any 'rootfs' device.
* The `tegra-flash-init` recipe was added to install a minimal init script for the flashing kernel, which sets up a USB mass storage gadget for the device to be flashed.  The serial number advertised by the gadget is the unique chip id (ECID) of the Tegra SoC.
* The `initrd-flash` script and the flashing kernel/initrd are added to the tegraflash package to drive the process.  The ECID (unique ID) of the SoC is extracted during initial RCM contact and used to locate the correct `/dev/sdX` device for the partition writing.
* A `find-jetson-usb` script has been added to wait for the appearance of the Jetson (in recovery mode) on the host USB bus.
* The tegraflash package generator in `image_types_tegra.bbclass` exports additional settings (e.g., the `TNSPEC_BOOTDEV` setting) in the file `.env.initrd-flash` for use by the `initrd-flash` script.
* The `tegra-bootfiles` recipe populates an external flash layout (XML) file in addition to the main (internal storage) flash layout file. The default layout from the L4T kit are modified, if required, to ensure that the boot and kernel partitions are present in the correct layout (with no duplicates) when `TNSPEC_BOOTDEV` set for using external storage.


# Notes
* RCM booting on T194 platforms bypasses the UEFI bootloader, directly loading the kernel from nvtboot.  This means that the kernel/initrd does not have access to any EFI variables.  UEFI *is* used in the RCM boot chain on T234 platforms.
* On Xavier NX dev kits (SDcard-based), you must still have an SDcard installed in the slot even if you are booting off an external drive. The SDcard must *not* have an `esp` or `APP` partition on it.  You must manually reformat the SDcard, as the flashing process will not do that for you.  For all other Jetsons with internal eMMC storage, the eMMC *will* be erased as part of the flashing process (and re-partitioned/re-populated for those platforms that store some of the bootloader binaries in the eMMC).
* Based on readings of some NVIDIA dev forum posts, A/B updates in JetPack 5.0 do not work properly in all cases when booting off an external drive.  That is supposed to be fixed in JetPack 5.1.
* Depending on your device's configuration (e.g., having multiple storage devices attached), you may need to manually configure the boot order in the UEFI bootloader by hitting `ESC` when UEFI starts, and then selecting `Boot Maintenance Manager`, then `Boot Options`, then `Change Boot Order`.  This is a limitation in JetPack 5.0 that is supposed to be fixed in JetPack 5.1.
* If you use a custom flash layout for your builds, note that there are some limitations on the composition of your flash layout file(s) due to how the bootloaders and the NVIDIA tools work. For example, you cannot use a SPI flash-only layout for internal storage, since the BUP payload generator expects to be able to create a payload containing the kernel/kernel DTB. The generator will fail during the build, since those partitions are not present in the SPI flash.  You also cannot use a single flash layout that includes only the boot partitions (in, for example, SPI flash on AGX Orin and Xavier NX) and the external storage device (`nvme`).  The tools that generate the MB1 BCT and/or MB2 BCT will error out because those bootloaders cannot access external storage. Hopefully NVIDIA will resolve these limitations in a future release.

# Comparison with stock L4T initrd flashing
* OE builds are per-machine, so much of the additional scripting to handle different targets during the flashing process can be omitted.
* With OE builds, TNSPEC_BOOTDEV selection is performed at build time. Switching back and forth between external rootfs and internal storage should be done with different builds.
* Stock L4T provides its initrd in prebuilt form, which requires disassembling and reassembling the initrd in the flashing scripts.  With OE, we can build the flashing initrd directly.
* Stock L4T requires customizing the external drive's flash layout to specify the exact size of the storage device, in sectors.  That's not required with OE builds, which do not use NVIDIA's flashing tools to partition the external drive.
* Stock L4T inserts udev rules on the host during flashing and does some network setup to talk to the device.  The process implemented for OE builds does not use any networking and does not require any udev rules changes during the flashing process.  You also don't have to be root to perform initrd-based flashing for OE builds, if you have followed the instructions [here](https://github.com/OE4T/meta-tegra/wiki/Flashing-the-Jetson-Dev-Kit#avoiding-sudo). (However, the `bmaptool copy` command used in the `make-sdcard` script does need root access for its setup, and the script will run it under `sudo` for you).

# Limitations on using an external drive for the rootfs
* On Jetson TX2 devices, the bootloaders do not have support for loading the kernel from an external drive.  The kernel, initrd, and device tree must reside on the eMMC (along with some of the boot partitions).
* Other Jetsons that boot directly from the eMMC (TX1, Nano-eMMC, Xavier NX-eMMC, AGX Xavier) also need to have some of the boot partitions in the main part of the eMMC.
* With Jetsons running JetPack 5/L4T R35.1.0, you may need to manually interrupt the UEFI bootloader to adjust the boot order to favor the external drive.  Even then, UEFI may attempt a PXE (network) boot first.  (This appears to be fixed with JetPack 5.1/L4T R35.2.1.)

# Known issues
* On an AGX Orin configured to use an external drive for the rootfs (NVMe), once it has been flashed using `initrd-flash`, the RCM boot of the `initrd-flash` kernel stops working; the NVMe-resident OS is booted instead.  This happens with the stock L4T initrd flashing tools also. To work around the problem, clear the partition table on the NVMe drive (e.g., using `sgdisk /dev/nvme0n1 --clear`) before resetting the Orin into recovery mode to start the re-flashing process.
* On T210 platforms (TX1/Nano), if you use the normal `doflash.sh` script, boot binaries will get overwritten (due to the way the NVIDIA flashing tools work), and that will cause an "FDT_ERR_BADMAGIC" error if you later try to run `initrd-flash`.  The error is minor, and probably won't cause any real issues with the flashing/booting process.  To be safe, though, you should not mix normal and initrd-based flashing in the same tegraflash directory.

# Customizing External Storage Size

Beginning with Jetpack 5.1.2 (r35.4.1) (and [this commit](https://github.com/OE4T/meta-tegra/commit/43e1bf6fb07650db1df1af575aa54cc0038481f2)), the `TEGRA_EXTERNAL_DEVICE_SECTORS` variable is used to customize the total size of device containing the root filesystem (as well as all other partitions in `PARTITION_LAYOUT_EXTERNAL`).  The default size of this variable assumes a device which is at least 64GB in size.

You may increase your root filesystem size to a value of around 30GB, leaving space for two root filesystem partitions (to support A/B redundancy) and additional partitions by defining `ROOTFSPART_SIZE` to a 4K aligned value in bytes of ~30 GB using a setting like `ROOTFSPART_SIZE = "30032384000"` in your local.conf.

If you have an external device larger than 64GB and would like to use this for a larger root filesystem, in addition to modifying `ROOTFSPART_SIZE` you will also need to adjust the `TEGRA_EXTERNAL_DEVICE_SECTORS` to specify a larger size in sectors.  For instance, to specify a ~60 GB rootfs on a 128 GB flash drive use `ROOTFSPART_SIZE = "60064768000"`and `TEGRA_EXTERNAL_DEVICE_SECTORS = "250000000"`

# General Tegraflash Troubleshooting

See https://github.com/OE4T/meta-tegra/wiki/Tegraflash-Troubleshooting