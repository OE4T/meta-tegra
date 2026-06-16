# Flashing Basics

As of L4T R39.2.0, all initial flashing is performed by "RCM booting" (booting over a USB link while the device is in ReCovery Mode) a
specialized Linux image that reads partition content over the USB link from the host and writes it to the correct location. The underlying
mechanism is different for Orin family vs. Thor family modules, but the end result is similar.

## Differences from stock Jetson Linux flashing

There are several differences between Jetson Linux and an operating system built using the
OpenEmbedded/Yocto Project tools, plus there are some differences in the way flashing is implemented
between the two environments.

### Jetson Linux flashing

* Jetson Linux is comprised of a collection of pre-built binary artifacts. The flashing process
contacts the device to identify which specific binaries should be used, assembles them, then writes them
to the device.

* Jetson Linux flashing scripts expect you to have your current working directory at the top-level
`Linux_for_Tegra` directory of the Jetson Linux/L4T kit, and to find the artifacts to flash
under that directory.

* The Jetson Linux flashing scripts are expected to be run under specific, supported Linux environments,
and can, more or less, require any available package to be installed. In addition, they expect to run
on a developer's workstation, and so can make changes to the host system's networking configuration, or
other system-level changes, and expect you to run all scripts under `sudo` to allow that.

### OE/Yocto flashing

* OE/Yocto builds are configured for a specific machine (i.e., a specific model of Jetson module or
dev kit). Some of the binary selection that happens during flashing with Jetson Linux instead happens
at build time.

* The `tegraflash` image type provided in the `meta-tegra` layer attempts to package nearly everything
you need to perform the flashing of your build into a tarball that you can unpack and use anywhere.

* The scripts provided by the `meta-tegra` layer attempt to minimize the number of additional dependencies on
external packages, and should be usable with any recent-vintage Linux distribution.

# Prerequisites

Before you get started, you will need:

* A suitable USB cable. All Orin and Thor family development kits have a USB-C connector for the
USB-OTG port. You don't need to have USB-C on your development host, though.
* A free USB port on your development host. Random failures may occur if you connect via an external hub.
* The `dtc` command (package `device-tree-compiler` on Debian/Ubuntu systems)
* The `cpp` command (from the GNU toolchain, package `cpp` on Debian/Ubuntu systems)
* The `bash` shell and a recent-vintage Python 3 installation are also needed for the scripts.
* The `.tegraflash-tar.zst` package for your target machine, generated from a Yocto build.

## Additional tools for Orin flashing:
For Orin targets, you will also need:
* The `sgdisk` command (from the `gdisk`/`gptfdisk` package)
* The `udisksctl` command (part of the `udisks2` package)
* The `bmaptool` command (part of the `bmap-tools` package)

Note that `bmaptool` isn't strictly required, but without it flashing even a moderately large rootfs image will take an extremely long time.

You should also disable automatic mounting of removable media in your desktop settings.
On recent Ubuntu (GNOME), go to Settings -> Removable Media, and check the box next to "Never prompt or start programs on media insertion."  You may also need to update the `/org/gnome/desktop/media-handling/automount` setting via `dconf`.  Check the setting with:
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

## Additional tools for Thor flashing:

NVIDIA implemented a different flashing process for the Thor module family and provides tools for it in the L4T kit. This same set of tools
is also used for flashing Thor-family modules with Yocto/OE builds. Note, however, that
if you are flashing a module that has been fused for secure boot, the scripts currently expect the `xmllint` command
(from package `libxml2-utils`).

## Serial console access

While not required, having a serial console connection is very helpful for troubleshooting flashing issues.

* AGX Orin development kits have a USB micro-B port that can be directly connected to your development host, exporting CDC-ACM serial connections over USB.
* AGX Thor development kits have a USB type-C port hidden under the lid above the ports on the back of the unit. Serial connections are exported via CDC-ACM over USB.
* The Orin Nano development kit has a "button header" connector that exposes 3.3V TTL UART signals. A USB-serial adapter can be used to connect these to a modern PC.

# The initrd-based flashing process

## Step 1 - Unpack the tegraflash tarball

To begin, unpack the `.tegraflash-tar.zst` file that was created by your image build into an empty directory. For example:

    $ MACHINE=jetson-orin-nano-devkit-nvme bitbake demo-image-base
	$ mkdir -p $HOME/scratch/flashing
	$ cd $HOME/scratch/flashing
	$ tar xf $BUILDDIR/tmp/deploy/images/jetson-orin-nano-devkit-nvme/demo-image-base-jetson-orin-nano-devkit-nvme.tegraflash-tar.zst

## Step 2 - Put the target in recovery mode

Next, make sure your target device is in recovery mode, with the USB OTG port connected to your host machine. You can use the command

    $ lsusb -d 0955:

to check this.


## Step 3 - Run the script

    $ ./initrd-flash

By default, the script flashes both the boot firmware to the QSPI flash and the rootfs to the external drive (or SDcard, for the stock Orin Nano dev kit). Flashing the boot firmware can take a long time, however, and isn't usually needed after the first flashing, so you can save time by adding the `--external-only` flag to skip the boot firmware update. Alternatively, you can write *just* the boot firmware by specifying the `--qspi-only` option.

## Step 4 - Reboot the device (Thor only)

For Thor-family devices, you must power cycle or reset the target after flashing is done.

# SDcard/External drive writing (Orin only)

For the Orin Nano development kit where you use the SDcard slot, or any Orin target where you use either a USB or NVMe external drive for the rootfs partition, you can use initrd flashing once (to ensure the boot firmware is at the correct version), then use either the `./dosdcard.sh` or `./doexternal.sh` script to write to a storage device that is directly connected to your host.  These scripts run the flashing helper script to assemble the partitions for the SDcard or external storage device, then runs a script to write those partitions to a storage device.

The `dosdcard.sh` script is created only for Orin Nano dev kit targets, since that is the only system that has an SDcard slot. To use it, insert a good-quality
SDcard (preferably 32G or larger) into an SDcard reader/writer on you host, and run `./dosdcard.sh /dev/sdXXX` or `sudo ./dosdcard.sh /dev/sdXXX`, specifying the actual name of your SDcard reader/writer device for `/dev/sdXXX`. The script will prompt you to confirm that you want to overwrite the contents of the card; you can add the `-y` option to skip the confirmation.

The `doexternal.sh` script is created for targets that are configured to boot off external (NVMe or USB) storage. To use this script, connect the storage device
that will be used in your Jetson system to a suitable interfacde on your host, and run `./dosdcard.sh /dev/sdXXX` or `sudo ./dosdcard.sh /dev/sdXXX`, specifying the actual name of the storage device for `/dev/sdXXX`. The script will prompt you to confirm that you want to overwrite the contents of the card; you can add the `-y` option to skip the confirmation.

**WARNING** Be absolutely sure that the device name you specify is correct! Do not accidentally erase/overwrite filesystems on your host system!

# General Troubleshooting Tips/Suggestions

Once you have it set up properly for your target hardware, the flashing process should "just work" most of the time. Getting that initial setup right
can sometimes be difficult, and the low-level tools used by the scripts often generate messages with confusing or undocumented error codes.

## Where to find information about the flashing process
1. The `initrd-flash` script typically displays just high-level status. If it fails, look in the `log.initrd-flash.DATE-TIME-STAMP` file that it creates
(the specific name is displayed when the script is finished).
2. For Thor-family flashing, the `initrd-flash` script hands off control to the NVIDIA "unified" flashing script. Adding the `--debug` (or `-D`) option
to `initrd-flash` enables more-verbose debug logging for those scripts, which can help with troubleshooting.
3. Monitor your host's kernel logs during the flashing process. USB issues can show up as kernel warnings.
4. If you can, monitor the serial console of the target device during the flashing process, so you can see what is happening on the device.

## USB communication errors
1. Sometimes USB problems are transient, and just power-cycling the target device and re-running the `initrd-flash` script will work.
2. Try swapping USB cables/ports, and ensure you are using a high quality cable.
3. If you are flashing after a soft reboot, try power cycling the device/entering tegraflash mode from power on instead of using `reboot forced-recovery`. There have been issues with the boot firmware not properly setting up for recovery mode after a soft reboot.


## Permissions failures
Try running the entire flashing script under `sudo`, especially if any error message mention permissions (although the script should be using `sudo` where it's needed).

## Storage layout issues
Suspect issues with partition table, especially if you've modified the partition table or increase sizes of partitions. Obscure errors like `cp: cannot stat 'signed/*': No such file or directory` typically mean you've got some problem with your custom partition table and/or target storage device size.

## Host environment issues
1. Use an x86-64 Linux host system for flashing, and always run the flashing scripts natively on that host. The low-level
tools that NVIDIA provides are binary-only, and may not operate on ARM or other non-x86 hosts.
2. Virtualization can interfere with the USB communication used by the flashing tools, so as mentioned above, run the flashing scripts natively, not in a virtual machine.
3. The TLP package (sometimes installed on notebooks/laptops to save battery power) can interfere with the flashing process. Use `sudo apt remove tlp` and reboot your host computer to remove it before flashing.
4. Use the `tar` command to unpack the tegraflash tarball, rather than a graphical file management tool. There have been issues in the past with file corruption caused by graphical tools.

## Try alternative setups

The [demo distro](https://github.com/OE4T/tegra-demo-distro) is built frequently, and images from those builds are tested often, so if you are unsure about
how your custom build environment might be affecting the flashing process, try building and flashing a `demo-base-image` from the demo distro for comparison. You can also compare against flashing using stock Jetson Linux/JetPack/SDK Manager.
