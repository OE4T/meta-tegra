While not enabled by default (except on the Jetsons that use the U-Boot bootloader), you can use the
L4T `extlinux.conf` support in your builds.

# For L4T R35.x and later

In the `kirkstone` and later branches based on the L4T R35.x and later series of releases, set `UBOOT_EXTLINUX = "1"` to configure the build to use an `extlinux.conf` file.  (As of 14 Apr 2024, `"1"` is now the default setting in the `master` branch.)

See the comments in [l4t-extlinux-config.bbclass](https://github.com/OE4T/meta-tegra/blob/master/classes-recipe/l4t-extlinux-config.bbclass) for additional configuration settings you can use.

## UBOOT_EXTLINUX_FDT

The `UBOOT_EXTLINUX_FDT` setting can be set to exactly `UBOOT_EXTLINUX_FDT = "/boot/${DTBFILE}"` before [https://github.com/OE4T/meta-tegra/pull/1968](https://github.com/OE4T/meta-tegra/pull/1968) or to any dtb file without full path (like `UBOOT_EXTLINUX_FDT = "${DTBFILE}"`) after [https://github.com/OE4T/meta-tegra/pull/1968](https://github.com/OE4T/meta-tegra/pull/1968) and backports.

When set, this adds a devicetree entry in the extlinux.conf file.  This setting is useful for easy testing of devicetree changes in the kernel and to support devicetree transitions on slot switch without capsule update.  Note that when `UBOOT_EXTLINUX` or `UBOOT_EXTLINUX_FDT` is not set, the `kernel-dtb` partitions defined in the root filesystem are ignored and the devicetree for the kernel is taken from the devicetree which is appended to the uefi image, therefore only updated when the uefi image is changed via tegraflash or capsule update.

`efivar -p --name 781e084c-a330-417c-b678-38e696380cb9-L4TDefaultBootMode` should return a value of `1` when using this feature. For additional context see [this thread](https://matrix.to/#/!YBfWVpJwNVtkmqVCPS:gitter.im/$x-m4h9rIYnwtMOaYtEkHg0a5HFzM4-mcpjoALOGkP4Y?via=gitter.im&via=matrix.org&via=3dvisionlabs.com) in element.

## UBOOT_EXTLINUX_FDTOVERLAYS

The PR at [https://github.com/OE4T/meta-tegra/pull/1968](https://github.com/OE4T/meta-tegra/pull/1968) adds support for specifying a list of overlays in your extlinux.conf file.  These overlays are also stored on the rootfs and applied to the kernel DTB at boot time after root slot selection.

This feature is only supported when `UBOOT_EXTLINUX_FDT` is specified.

To use, specify

```
UBOOT_EXTLINUX_FDT = "${DTBFILE}"
UBOOT_EXTLINUX_FDTOVERLAYS = "my-overlay.dtbo"
```
Where `"my-overlay.dtbo"` is an overlay built using the mechanisms specific to your branch implementation (or potentially one provided by NVIDIA.  See [Using-device-tree-overlays](Using-device-tree-overlays.md) for more details.  Note that since the overlay only happens to the kernel DTB this mechanism cannot be used to make any changes to the UEFI DTB.

## Caveats

* The upstream UEFI bootloader does not implement this; it was tacked on by NVIDIA in their `L4TLauncher` EFI application.
* The ext4 filesystem implementation that NVIDIA provides in their bootloader may have some bugs/limitations that could prevent it from reading the `extlinux.conf` or other files in your root filesystem.  Using newer ext4 features, or non-ext4 filesystems for your root filesystem, could lead to boot failures.
* The `extlinux.conf` syntax supported in `L4TLauncher` is not the same as U-Boot's, and the parsing code isn't the most robust/forgiving, so be careful about any modifications you may want to make, to avoid boot failures.

# For L4T R32.x

In L4T R32.x:

* The TX1/Nano platforms use U-Boot by default, so no changes are required to use `extlinux.conf` files. 
* The TX2 platform defaults to using U-Boot which supports `extlinux.conf`.  TX2 builds can be configured to use `cboot` without U-Boot, and the TX2 `cboot` implementation does not support `extlinux.conf`.
* The Xavier platforms have a different `cboot` code base which (unlike the TX2 implementation) *does* have some support for `extlinux.conf` files.  The rest of this page covers the Xavier implementation.

## Configuring Xavier extlinux.conf support
Add the `cboot-extlinux` package to your image to enable booting your Xavier device
with the kernel loaded from `/boot` in the rootfs instead from from a separate partition.
This is only available in the `kirkstone-l4t-r32.7.x` branch (as of this writing).

**Use with caution.**  Not recommended for production use.

### Notes

The cboot bootloader on the Xavier (t194) platforms has support for loading the kernel, initial ramdisk,
and device tree from files in the rootfs, rather than the `kernel` partition. The stock L4T BSP has
supported this for several releases, installing the kernel image and initrd into `/boot` and
a `/boot/extlinux/extlinux.conf` file that cboot uses to locate the files. This can simplify kernel
development by eliminating the need to reflash the device to boot with updated kernels.

To implement this in meta-tegra, the `cboot-extlinux` recipe has been added.  Adding `cboot-extlinux` to your
image will include the necessary files -- kernel, initrd (if not bundled), and optionally the
device tree, along with the `extlinux.conf` file and signatures for the files that are expected to
be signed -- in your rootfs.

When extlinux support in cboot is enabled (which it is by default), cboot will first try to mount the rootfs to locate
the `extlinux.conf` file.  The rootfs is either marked as such with a partition GUID (see below) or is assumed
to be the first partition on the boot medium (SDcard, eMMC, or external device). cboot then tries to open
`/boot/extlinux/extlinux.conf` on that filesystem. If successful, it parses the configuration, then attempts
to load the kernel, initrd, and/or device tree based on the path names in the file. For elements that are
not configured in that file (or all of them, if the file does not exist), cboot falls back to loading them
from partitions on the device (`kernel` for the kernel+initrd, `kernel-dtb` for the device tree).

### extlinux.conf file format

The format of the configuration file is a subset of the format used in the
[distro boot](https://source.denx.de/u-boot/u-boot/-/blob/master/doc/README.distro)
feature of U-Boot.  The `cboot-extlinux-config.bbclass` file implements the cboot-specific configuration subset; see the comments in that
file for more information.

**WARNING** Modifying the `extlinux.conf` file incorrectly will often result in
cboot crashes, making your device unbootable. Use caution when making any changes
to the file.

### Adding the device tree

By default, the `cboot-extlinux` recipe installs the default kernel image and initrd
(if configured to be separate from the kernel), but not the device tree, to align
with the default stock L4T setup. Set `UBOOT_EXTLINUX_FDT = "/boot/${DTBFILE}"` in
either a bbappend or in your `local.conf` to include the device tree.

### Incompatible with A/B redundancy

Using `cboot-extlinux` for loading the kernel is not compatible with the A/B
redundancy mechanism - the kernel will always be loaded from the A rootfs partition.

It may be possible to fix this by assigning a unique partition GUID to each of
the two rootfs partitions, and creating cboot options files (`cbo.dtb` files) to
configure the rootfs GUIDs - one to be loaded into the `CPUBL-CFG` partition, and
the other into `CPUBL-CFG_b`. However, that would conflict with the normal bootloader
update mechanism, since BUP payloads don't distinguish between the A and B slot for their
content. Some extra mechanism would be needed to keep the two CPUBL-CFG partitions
synchronized with the corresponding rootfs partition GUIDs.

### Filesystem restrictions

This has only been tested with ext4-formatted root filesystems, and bugs found in
cboot's ext4 implementation have been patched to make this work. Other filesystem types
are unlikely to work.  Also, you should use the `cboot-t19x` recipe that builds
cboot from source to get the required patches (this is the default).

