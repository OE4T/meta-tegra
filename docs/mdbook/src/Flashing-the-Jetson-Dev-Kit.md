This total-beginner's guide will walk you through the process of flashing a newly-generated image your Jetson development kit! The instructions
here are for branches based off L4T R32.4.3 and later. (For earlier releases, click the revisions count, under the title, to go back to an
earlier revision of the page.)

# Initrd Flashing
For branches based off L4T R35.1.0 and later (`master`, `kirkstone`, and `langdale`), and the `kirkstone-l4t-r32.7.x` branch, an alternative flashing process (called "initrd flashing") is available, which supports flashing to a rootfs (`APP` partition) on an external storage device.  See [this page](https://github.com/OE4T/meta-tegra/wiki/initrd-flashing-support) for more information.

The table below helps outline the flashing mechanism(s) supported depending on target root filesystem storage for all recent branches (`kirkstone-l4t-r32.7.x` and later)

| Target Rootfs Storage | Flashing method |
| --------------------- | ------------- |
| on-board eMMC  | `doflash.sh` or [initrd-flash](https://github.com/OE4T/meta-tegra/wiki/initrd-flashing-support)  |
| SDCard | `doflash.sh` or [initrd-flash](https://github.com/OE4T/meta-tegra/wiki/initrd-flashing-support). `dosdcard.sh` may be used for subsequent programming after initial bootloader programming with `doflash.sh` or `initrd-flash`. |
| NVMe | [initrd-flash](https://github.com/OE4T/meta-tegra/wiki/initrd-flashing-support)  |
| M.2 drive or SATA drive | [initrd-flash](https://github.com/OE4T/meta-tegra/wiki/initrd-flashing-support) |

## Prerequisites

Before you get started, you'll need the following:

* A suitable USB cable. For most Jetsons, this is a type A to micro-B cable, but for the AGX Xavier and AGX Orin dev kits, you'll need a USB-C cable (or a USB-C to type A cable, if your development host does not have USB-C ports). As NVIDIA mentions in their documentation, it's important to use a good-quality cable for successful flashing.

* A free USB port on your development machine. The flashing tools work best if you can connect directly to a port on your system,
rather than using a USB hub.

* For L4T R32.5.0 and later, you must have the `dtc` command in your PATH, since the NVIDIA tools use that command when preparing
the boot files for some of the Jetsons. On Ubuntu systems, that command is provided by the `device-tree-compiler` package.

* For L4T R35 and later, you must have the GNU `cpp` command in your PATH (and **not** the LLVM/Clang `cpp`, see #1959).

While not required, a serial console connection is very useful, particularly with troubleshooting flashing problems, since
the bootloaders only write messages to the serial console.

Please note, also, that flashing typically does **not** work from a virtual machine. You should be running the flashing tools directly on a Linux host.

### For SDcard-based development kits

If you have a Jetson Nano or Jetson Xavier NX development kit, you'll need a good-quality MicroSDHC/SDXC card, preferably
16GB or larger. Higher-speed cards (at least UHS-I) are preferred, particularly if you plan to program the SDcard through
an SDcard reader/writer on your development host. The reader/writer should be high-speed also, and connected through a
high-speed I/O interface (e.g., USB 3.1).

Programming an SDcard in a reader/writer attached to your host is also faster (*much* faster) if you have the `bmaptool`
command in your PATH. On Ubuntu systems, that command is provided by the `bmap-tools` package. (But note that `bmaptool`
requires `sudo`.)

The Jetson AGX Xavier development kit also supports booting from a MicroSD card instead of the on-board eMMC, with some
limitations.

### Avoiding sudo

You can avoid using `sudo` during the flashing/SDcard writing process (except for using `bmaptool`, as noted above)
by adding yourself to suitable groups and installing a `udev` rules file to give yourself access to the Jetsons via
USB. The following instructions are for Ubuntu; other distros may have other groups or require additional setup.

* For SDcard writing, add yourself to group `disk`.
* For USB flashing, add yourself to group `plugdev`,

You can use [this script](https://github.com/OE4T/tegra-demo-distro/blob/master/layers/meta-tegrademo/scripts/setup-udev-rules) to
install the `udev` rules that grant the `plugdev` group write access to the Jetson devices when they are connected
in recovery mode to your development host.

Note that after changing your group membership and/or `udev` rules, you may need to reboot your development
host for the changes to take effect. It's worth this extra setup, though, to eliminate the need for root access.


## Building a tegraflash package

All of the Jetson machine configurations add a `tegraflash` image type by default, which generates a compressed tarball
contains all of the files, tools, and scripts for flashing the device and/or creating a fully-populated SDcard. If you've
successfully run a bitbake build of an image, you should see a file called

    <image-type>-${MACHINE}.tegraflash.tar.gz

or, in more recent branches,

    <image-type>-${MACHINE}.rootfs.tegraflash.tar.<compression>

in the directory `$BUILDDIR/tmp/deploy/images/${MACHINE}`. where `<compression>` could be either `gz` or `zst`, depending on the branch you are using (zstd replaced gzip as the default compression method in Feb 2025).

### Using an SDcard with the Jetson AGX Xavier

By default, the `tegraflash` package for the AGX Xavier is set up for flashing the on-board eMMC. If you want to
boot your Xavier off an SDcard instead, you should add the following to your build configuration (e.g., in
`$BUILDDIR/conf/local.conf`):

      TEGRA_ROOTFS_AND_KERNEL_ON_SDCARD = "1"
      ROOTFSPART_SIZE = "15032385536"

The `ROOTFSPART_SIZE` setting is for a 16GB SDcard; adjust the size as needed for a larger or smaller card.

With these settings in place, the resulting `tegraflash` package supports flashing the bootloader file to
the on-board eMMC, moving the kernel, device tree, and rootfs on the SDcard. Note that this is *only* supported
for the Jetson AGX Xavier, and that SDcard booting *does not* support the bootloader redundancy features.

With this configuration, there will be two scripts in the `tegraflash` package: `dosdcard.sh` for writing the
SDcard, and `doflash.sh` for flashing the bootloader partitions to the eMMC.  Run the `dosdcard.sh` script to
format and write the SDcard on your development host, insert the SDcard into the slot on the AGX Xavier dev kit,
then use the `doflash.sh` to flash the bootloader partitions. (Unlike for Xavier NX devices, you *must* perform
these steps separately.)

## Unpacking the tegraflash package

To flash your Jetson, or create an SDcard image, create an empty directory and use the `tar` command to
unpack the `tegraflash` package into it:

      $ mkdir ~/tegraflash
      $ cd ~/tegraflash
      $ tar -x -f $BUILDDIR/tmp/deploy/images/${MACHINE}/<image-type>-${MACHINE}.tegraflash.tar.gz

Be sure to use the `tar` command from a terminal window. Some users have reported issues with incorrect
results when extracting files using GUI-based tools.

## Setting up for flashing

1. Start with your Jetson powered off. (NVIDIA recommends connecting hardware only while the device is powered off.)
2. Connect the USB cable from your Jetson to your development host.
3. Insert an SDcard into the slot on the module, if needed.
4. Power on the Jetson and put it into recovery mode.

For SDcard-based Jetsons (Nano and Xavier NX), you have the option of programming the SDcard contents
either during USB flashing or separately using an SDcard reader/writer on your development host. If
you program the SDcard separately, perform that step first and insert the already-programmed card
into the slot on the module in step 3 above. (When using an SDcard with the AGX Xavier, you *must*
pre-program the SDcard first.)

To verify that the device is in recovery mode and that the USB cable is connected properly, use the
following command:

      $ lsusb -d 0955:
      Bus 001 Device 006: ID 0955:7c18 NVIDIA Corp. T186 [TX2 Tegra Parker] recovery mode

If you don't see your Jetson listed, double-check the cable and try the recovery mode sequence
again.

### Recovery mode jumpers and buttons

The different Jetson develpoment kits have different mechanisms for entering recovery mode.

#### Jetson TX1 and TX2 development kits

Press *and hold* the REC ("recovery") button, press and release the RST ("reset") button. Continue
to hold the REC button for 3-4 seconds, then release.
[[images/TX1-TX2-Devkit-RecoveryMode-Button.jpg|alt=TX1-TX2 buttons]]

#### Jetson AGX Xavier development kit

Press *and hold* the center button, and press and release the reset button (on the right).
[[images/AGX-Xavier-RecoveryMode-Button.jpg|alt=AGX Xavier buttons]]

#### Jetson Orin development kit

Press *and hold* the center button. Then plug in the power supply. Release the center button.  Note that it can take 10-15 seconds for the device to fully enter recovery mode and export its serial console after power up.

#### All Jetson Nano, Xavier NX development kits

Connect a jumper between the 3rd and 4th pins from the right hand side of "button header"
underneath the back of the module (FRC and GND; see the labeling on the underside of the
carrier board). The module will power up in recovery mode automatically.
[[images/Nano-NX-RecoveryMode-Jumper.jpg|alt=Nano-Xavier pins]]

For the older Jetson Nano rev A02 carrier boards, the FRC pin is in the 8-pin header next to
the module, beside the MIPI-CSI camera interface. The pins are labeled on the underside of
the carrier board.
![Nano A02 pins](https://user-images.githubusercontent.com/227565/112763078-4475dd80-8fc0-11eb-8670-a2d13f0c5083.png)

## Writing an SDcard

If you want to program the SDcard contents directly onto the card from your development host:

1. Insert the card into the reader/writer on your host.
2. *Carefully* determine the device name for the card. **Using the wrong device name could destroy your host's filesystems.**
3. Run the `dosdcard.sh` script to program the card.

Here is an example, for a system where `/dev/sda` is the device name of the card:

    $ ./dosdcard.sh /dev/sda

Remember to use `sudo`, if needed. The script will ask you to confirm before writing (which you can
skip by adding `-y` to the command above).

### Creating an SDcard image

You can also create an SDcard image file that can later be written to one or more cards:

    $ ./dosdcard.sh <filename>

The resulting file will be quite large, and writing the image can take a long time.

### SPI flash on SDcard-based kits

The SDcard-based development kits store some (in some cases, all) of the bootloader
content on a SPI flash device on the Jetson module. You must ensure that the bootloader content 
in this flash device is compatible with the layout on the SDcard you create, since the
early-stage boot data is programmed with the locations/sizes of SDcard-resident partitions,
and cannot read the GPT partition table at runtime. To do this, you must perform a USB flash
to program the SPI flash **at least once** on you development kit, by following the
steps in the next section.

Once the SPI flash has been programmed correctly, you should be able to update just
by writing new SDcard images unless you make changes in your build that affect one
of the boot-related partitions residing in the SPI flash, or change the flash layout
XML in a way that alters the location/size of one of the SDcard-resident boot partitions
(if there are any).

## Flashing the Jetson

Once everything is set up, use the `doflash.sh` script to program the Jetson:

    $ ./doflash.sh

Remember to use `sudo` to invoke the script, if needed.  If successful, the Jetson will be rebooted
into your just-built image automatically after flashing is complete.

For SDcard-based development kits, you can program *just* the boot partitions in the SPI flash with:

    $ ./doflash.sh --spi-only

You should insert your programmed SDcard in the slot on the Jetson before performing this step, so
when the Jetson reboots after the flashing process completes, it will boot into your image.

## Automating Unpack and Flash Steps

You can use [this script](https://github.com/OE4T/tegra-demo-distro/blob/master/layers/meta-tegrademo/scripts/oe4t-tegraflash-deploy) if desired to automate the steps associated with unpacking and running the `./doflash.sh` script for tegraflashing.

## Issues during flashing
If you run ```sudo ./doflash.sh``` and flashing is started but then it hang in some step like:
```
[   1.7586 ] Flashing the device
[   1.7611 ] tegradevflash --pt flash.xml.bin --storageinfo storage_info.bin --create
[   1.7636 ] Cboot version 00.01.0000
[   1.7659 ] Writing partition GPT with gpt.bin
[   1.7666 ] [................................................] 100%
[   1.7707 ] Writing partition PT with flash.xml.bin
[  15.9892 ] [................................................] 100%
[  15.9937 ] Writing partition NVC with nvtboot.bin.encrypt
[  16.2433 ] [................................................] 100%
[  16.2569 ] Writing partition NVC_R with nvtboot.bin.encrypt
[  26.2706 ] [................................................] 100%
[  26.2877 ] Writing partition VER_b with jetson-nano-qspi-sd_bootblob_ver.txt
[  36.3103 ] [................................................] 100%
[  36.3202 ] Writing partition VER with jetson-nano-qspi-sd_bootblob_ver.txt
[  36.5833 ] [................................................] 100%
[  36.5927 ] Writing partition APP with test-image.ext4.img
[  36.8548 ] [................................................] 100%
```

or if e.g following:
```
[   1.9394 ] 00000007: Written less bytes than expected
[  21.7219 ] 
Error: Return value 7
Command tegradevflash --pt flash.xml.bin --storageinfo storage_info.bin --create
```

It's good to connect serial console which in above case will print something like:
```
[0020.161] device_write_gpt: Erasing boot device spiflash0
[0039.824] Erasing Storage Device
[0039.827] Writing protective mbr
[0039.833] Error in command_complete 18003 int_status
[0039.840] Error in command_complete 18003 int_status
[0039.847] Error in command_complete 18003 int_status
[0039.852] sending the command failed 0xffffffec in sdmmc_send_command at 109
[0039.859] switch command send failed 0xffffffec in sdmmc_send_switch_command at 470
[0039.866] switch cmd send failed 0xffffffec in sdmmc_select_access_region at 1301
[0039.876] Error in command_complete 18001 int_status
[0039.883] Error in command_complete 18001 int_status
[0039.890] Error in command_complete 18001 int_status
[0039.895] sending the command failed 0xffffffec in sdmmc_send_command at 109
[0039.902] setting block length failed 0xffffffec in sdmmc_block_io at 945
[0039.909] block I/O failed 0xffffffec in sdmmc_io at 1215
[0039.914] block write failed 0xffffffec in sdmmc_bdev_write_block at 178
[0039.921] device_write_gpt: failed to write protective mbr
[0039.926] Number of bytes written -20
[0039.930] Written less bytes than expected with error 0x7
[0039.935] Write command failed for GPT partition
```

Things to try:
* ! USB cable must be plugged directly to PC host (don't use USB hub otherwise issues like described above will appear) !
* verify USB cable quality (try to use another one)
* power off/on device and try flashing again

# General Tegraflash Troubleshooting

See https://github.com/OE4T/meta-tegra/wiki/Tegraflash-Troubleshooting