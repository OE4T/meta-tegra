While not enabled by default (except on the Jetsons that use the U-Boot bootloader), you can use the
L4T `extlinux.conf` support in your builds.

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

### extlinux.conf file format

The format of the configuration file is a subset of the format used in the
[distro boot](https://source.denx.de/u-boot/u-boot/-/blob/master/doc/README.distro)
feature of U-Boot.  The `cboot-extlinux-config.bbclass` file implements the cboot-specific configuration subset; see the comments in that
file for more information.

**WARNING** Modifying the `extlinux.conf` file incorrectly will often result in
cboot crashes, making your device unbootable. Use caution when making any changes
to the file.

