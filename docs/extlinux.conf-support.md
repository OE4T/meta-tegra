# extlinux.conf support

L4T `extlinux.conf` support is implemented by NVIDIA's `L4TLauncher` UEFI application, not the upstream UEFI specification. It is enabled by default on all Tegra machines: `UBOOT_EXTLINUX = "1"` is set in `tegra-common.inc` and causes `l4t-launcher-extlinux` to be added as a runtime image dependency. Set `UBOOT_EXTLINUX = "0"` to disable it.

L4TLauncher reads the `L4TDefaultBootMode` EFI variable (GUID `781e084c-a330-417c-b678-38e696380cb9`) to determine the preferred boot mode. A value of `1` selects extlinux.conf-based boot. This is set at flash time and can be inspected on the target with:

```
efivar -p --name 781e084c-a330-417c-b678-38e696380cb9-L4TDefaultBootMode
```

This reflects the *preferred* mode. If `extlinux.conf` cannot be found or read, L4TLauncher falls back to partition-based boot automatically.

The ext4 implementation in L4TLauncher may have bugs or limitations that prevent it from reading `extlinux.conf` or other rootfs files when newer ext4 features are in use. Non-ext4 root filesystems are unlikely to work.

## Configuration variables

These variables are typically set in a machine or distro configuration file (a `.conf` file in `conf/machine/` or `conf/distro/`, or a layer's `layer.conf`). They can also be set in `local.conf` for local development. Variables that control which DTB or overlays are deployed belong in machine configuration alongside `KERNEL_DEVICETREE`.

### UBOOT_EXTLINUX_FDT

When set, adds an `FDT` directive to `extlinux.conf` pointing to the specified device tree file. The file is installed to `/boot/dtb/` on the rootfs.

This is useful for testing devicetree changes in the kernel and for supporting devicetree transitions on slot switch without a capsule update. When not set, the devicetree for the kernel is taken from the `kernel-dtb` partition, which is only updated via tegraflash or capsule update.

Set it to the DTB filename (no path prefix):

```
UBOOT_EXTLINUX_FDT = "tegra234-p3701-0000-p3737-0000.dtb"
```

### UBOOT_EXTLINUX_FDTOVERLAYS

A space-separated list of device tree overlay files to apply at boot time. The overlays are installed to `/boot/` on the rootfs and listed in an `OVERLAYS` directive in `extlinux.conf`. L4TLauncher applies them to the kernel DTB late in the boot sequence. Only kernel DTB modifications are supported, as the UEFI DTB cannot be changed this way.

Overlays can be standard DTB overlays (applied unconditionally) or plugin-manager-format overlays (which contain conditional directives evaluated against the hardware configuration at boot time).

`UBOOT_EXTLINUX_FDT` must be set for overlays to be applied. L4TLauncher only processes the `OVERLAYS` directive when it has loaded a DTB from the rootfs via the `FDT` directive. Without `FDT`, overlays are silently ignored.

```
UBOOT_EXTLINUX_FDT = "tegra234-p3701-0000-p3737-0000.dtb"
UBOOT_EXTLINUX_FDTOVERLAYS = "my-overlay.dtbo"
```

See [Using-device-tree-overlays](Using-device-tree-overlays.md) for more details on building and using overlays. An advantage of rootfs-based overlays over SPI flash overlays is that they can be updated with the rootfs, without a capsule update or tegraflash.

### UBOOT_EXTLINUX_KERNEL_ARGS

Kernel command line arguments written to the `APPEND` line in `extlinux.conf`. Defaults to `${KERNEL_ARGS}`, which is set in each machine configuration to provide platform-specific arguments such as console device and memory settings. Override or append to this variable to add arguments beyond what the machine configuration provides.

```
UBOOT_EXTLINUX_KERNEL_ARGS:append = " systemd.log_level=debug"
```

### UBOOT_EXTLINUX_MENU_TITLE

The `MENU TITLE` line written to `extlinux.conf`. Defaults to `"L4T boot options"`. This line is required by L4TLauncher.

### UBOOT_EXTLINUX_TIMEOUT

Time in tenths of a second to display the boot menu before selecting the default entry. Not set by default, meaning the default entry is selected immediately without showing the menu. Useful during development to allow manual boot entry selection.

```
UBOOT_EXTLINUX_TIMEOUT = "30"
```

### L4T_UBOOT_EXTLINUX_EXTRA_FDTS

A space-separated list of additional DTB files to install to `/boot/dtb/` alongside any DTB configured via `UBOOT_EXTLINUX_FDT`. These are included in the base `l4t-launcher-extlinux` package and are not referenced in `extlinux.conf`. These are distinct from the files in the `l4t-launcher-extlinux-dtb-extra` subpackage, which contains all remaining staged DTBs and DTBOs not claimed by the base package. See [Jetson expansion header configuration](#jetson-expansion-header-configuration-jetson-io).

### EXTERNAL_KERNEL_DEVICETREE

Path to a directory containing DTBs and DTBOs from an external device tree provider (i.e. when `PREFERRED_PROVIDER_virtual/dtb` is set). Defaults to `${RECIPE_SYSROOT}/boot/devicetree` when a `virtual/dtb` provider is configured, empty otherwise. The recipe stages all `.dtb` and `.dtbo` files found here. Any not claimed by `UBOOT_EXTLINUX_FDT`, `UBOOT_EXTLINUX_FDTOVERLAYS`, or `L4T_UBOOT_EXTLINUX_EXTRA_FDTS` end up in the `l4t-launcher-extlinux-dtb-extra` subpackage. This variable does not normally need to be set manually.

### L4T_EXTLINUX_BASEDIR

The root directory for all files installed by `l4t-launcher-extlinux`. Defaults to `/boot`. Changing this would require a corresponding patch to `edk2-nvidia` and is not recommended.

## Jetson expansion header configuration (jetson-io)

NVIDIA's `jetson-io` tool allows runtime reconfiguration of the Jetson expansion header pins. It generates device tree overlay files for the selected pin configuration and writes them to the rootfs, then updates `extlinux.conf` to reference the new overlays via the `OVERLAYS` directive, causing them to be applied at the next boot. The `python3-jetson-io` recipe packages this tool for use in meta-tegra builds.

To include it in your image:

```
IMAGE_INSTALL:append = " python3-jetson-io"
```

The recipe depends on `l4t-launcher-extlinux` and recommends `l4t-launcher-extlinux-dtb-extra`. The `dtb-extra` subpackage contains all available DTBs (from `/boot/dtb/`) and DTBOs (from `/boot/`) that were not explicitly configured via `UBOOT_EXTLINUX_FDT` or `UBOOT_EXTLINUX_FDTOVERLAYS`. `jetson-io` uses these files to enumerate available hardware configurations and generate overlay files.

For `jetson-io` to write the resulting overlay back into the extlinux boot configuration, `UBOOT_EXTLINUX` must be enabled (it is by default) and the rootfs must be accessible from the bootloader. See the NVIDIA developer guide for [Configuring the Jetson Expansion Headers](https://docs.nvidia.com/jetson/archives/r39.2/DeveloperGuide/HR/ConfiguringTheJetsonExpansionHeaders.html) for instructions on running the tool on the target.

**Note:** `jetson-io` modifies the device tree configuration at runtime on the target device. Its output is not reproducible at build time and is intended for development and devkit use, not production images.

## Caveats

* The `extlinux.conf` syntax supported in L4TLauncher is not the same as U-Boot's, and the parsing code is not particularly robust, so be careful about any modifications to avoid boot failures.
* All paths in `extlinux.conf` must be absolute. Relative paths are not supported by L4TLauncher.
* `FDTDIR` is not supported.
* `UBOOT_EXTLINUX_CONSOLE` is not used. Console settings are provided via `KERNEL_ARGS` in the machine configuration.
* The kernel image directive in the generated file uses `LINUX` rather than `KERNEL`.
