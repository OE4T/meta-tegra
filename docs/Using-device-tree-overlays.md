For many L4T/Jetson Linux releases, NVIDIA has provided a mechanism (the `jetson-io` scripts) for applying device tree overlays (`.dtbo` files) dynamically at runtime.

For `scarthgap` and other JetPack 6 / L4T R36.x branches, the NVIDIA device trees are no longer built from the in-kernel device tree sources. That means the older `KERNEL_DEVICETREE_APPLY_OVERLAYS` flow is not the normal path on this branch.

If you need to customize device trees on `scarthgap`, choose the mechanism that matches when the change needs to happen:

* Use `PREFERRED_PROVIDER_virtual/dtb` when you want to replace or derive the kernel DTB at build time.
* Use `TEGRA_PLUGIN_MANAGER_OVERLAYS` when you want UEFI to apply overlays stored in SPI flash at runtime.
* Use `UBOOT_EXTLINUX_FDT` together with `UBOOT_EXTLINUX_FDTOVERLAYS` when you want overlays stored in the rootfs and applied to the kernel DTB at boot time.

# Current guidance for scarthgap / JetPack 6 (L4T R36.x)

## Build-time customization with an out-of-tree device tree provider

For L4T R36.x, `nvidia-kernel-oot-dtb` is the default `virtual/dtb` provider for Jetson platforms. If you need your own DTB, set `PREFERRED_PROVIDER_virtual/dtb` to a recipe that provides your customized device tree.

If that recipe needs to apply overlays, do it as part of the recipe build, for example by adding `fdtoverlay` invocations in a `bbappend` for `nvidia-kernel-oot` or in your custom provider recipe. This is the normal `scarthgap` path when you need to modify or replace the active kernel DTB at build time.

### Example out-of-tree devicetree in tegra-demo-distro

See the tegra-demo-distro example at [meta-tegrademo/recipes-bsp/tegrademo-devicetree](https://github.com/OE4T/tegra-demo-distro/tree/scarthgap/layers/meta-tegrademo/recipes-bsp/tegrademo-devicetree), which shows how to modify a base device tree from `nvidia-kernel-oot` to one specific to your hardware platform. This simple example just adds a single `compatible` entry to the base device tree.

1. Determine which device tree is currently in use. One way to do this is with `bitbake -e <your image>` and the value of `KERNEL_DEVICETREE`.
2. Determine whether there is already an example in [meta-tegrademo/recipes-bsp/tegrademo-devicetree](https://github.com/OE4T/tegra-demo-distro/tree/scarthgap/layers/meta-tegrademo/recipes-bsp/tegrademo-devicetree) that uses your current `KERNEL_DEVICETREE` as a base. Current examples include:
   * `tegra234-p3768-0000+p3767-0005-oe4t.dts`: `jetson-orin-nano-devkit` or `jetson-orin-nano-devkit-nvme` on a p3768 carrier
   * `tegra234-p3768-0000+p3767-0000-oe4t.dts`: Jetson Orin NX 16GB on a p3768 carrier
   * `tegra234-p3737-0000+p3701-0000-oe4t.dts`: `jetson-agx-orin-devkit`
3. If there is not an existing device tree built from your base `KERNEL_DEVICETREE`, follow those examples to add one to `SRC_URI` and to the recipe sources.
4. Set your DTB provider and machine-specific `KERNEL_DEVICETREE` to use the generated DTB:

```
PREFERRED_PROVIDER_virtual/dtb = "tegrademo-devicetree"
KERNEL_DEVICETREE:jetson-orin-nano-devkit-nvme = "tegra234-p3768-0000+p3767-0005-oe4t.dtb"
KERNEL_DEVICETREE:jetson-orin-nano-devkit = "tegra234-p3768-0000+p3767-0005-oe4t.dtb"
```

Where `KERNEL_DEVICETREE` overrides the setting for your machine and references the generated `.dtb` file instead of the `.dts` source.

## Runtime overlays stored in SPI flash

This mechanism is supported in branches based on L4T R35.x and later. Overlays are appended to the kernel DTB by the NVIDIA flashing/signing tools and are applied by the UEFI bootloader at runtime. The overlays are stored in SPI flash and are only updated by tegraflash or capsule update.

### Locating overlays

The exact list of overlays supplied by NVIDIA varies by target platform. On R35.x-based branches, you can find them by building the kernel recipe (`virtual/kernel` or `linux-tegra`) and examining its output under `${BUILDDIR}/work/tmp/${MACHINE}/linux-tegra`. For R36.x-based branches, device trees are built as part of `nvidia-kernel-oot` and exposed through the `virtual/dtb` provider.

### Applying overlays

Append your additional overlays to the `TEGRA_PLUGIN_MANAGER_OVERLAYS` variable, which consists of a blank-separated list of `.dtbo` file names. You can do this in your machine configuration file, or add it to the `local.conf` file in your build workspace. That variable is set by the layer to include overlays that NVIDIA requires for its platforms, so be sure to append to it rather than overwrite it.

### Example

To configure the pins on the 40-pin expansion header of the Jetson Orin Nano development kit, add the following line to your `$BUILDDIR/conf/local.conf` file:

        TEGRA_PLUGIN_MANAGER_OVERLAYS:append:jetson-orin-nano-devkit = " tegra234-p3767-0000+p3509-a02-hdr40.dtbo"

# Runtime overlays stored in the rootfs partition

Support is available to apply overlays from the rootfs using the `OVERLAYS` option in `extlinux.conf`. This is useful when you want overlay selection to live with a rootfs slot instead of in SPI flash.

This mechanism only affects the kernel DTB, because the overlay application happens after the UEFI DTB has already been selected.

To use it, enable `extlinux.conf` support and specify both the base DTB and the overlays:

```
UBOOT_EXTLINUX = "1"
UBOOT_EXTLINUX_FDT = "${DTBFILE}"
UBOOT_EXTLINUX_FDTOVERLAYS = "tegra234-p3767-0000+p3509-a02-hdr40.dtbo"
```

See [this section](extlinux.conf-support.md#uboot_extlinux_fdtoverlays) of the extlinux.conf documentation for more details about configuring `OVERLAYS` in `extlinux.conf`.

# Legacy build-time application of overlays (L4T R32.6.x through R35.x only)

This mechanism is supported in branches based on L4T R32.6.x through R35.x only. Overlays are applied to the device tree during the kernel build, directly modifying the kernel DTB. This is not the normal approach on `scarthgap`, where the NVIDIA device trees are no longer built from the in-kernel device tree sources.

## Locating overlays

The exact list of overlays supplied by NVIDIA varies by target platform. You can find them by building the kernel recipe (`virtual/kernel` or `linux-tegra`) and examining its output under `${BUILDDIR}/work/tmp/${MACHINE}/linux-tegra`.

## Applying overlays

Set the `KERNEL_DEVICETREE_APPLY_OVERLAYS` variable to a blank-separated list of `.dtbo` file names to have those overlays applied during the kernel build. You can do this in your machine configuration file, or add it to the `local.conf` file in your build workspace.

### Example

To configure a Jetson Xavier NX development kit for IMX477 and IMX219 cameras, add the following line to your `$BUILDDIR/conf/local.conf` file:

        KERNEL_DEVICETREE_APPLY_OVERLAYS:jetson-xavier-nx-devkit = "tegra194-p3668-all-p3509-0000-camera-imx477-imx219.dtbo"

## Other possible use cases

For U-Boot-based Jetsons (only supported on a subset of Jetson modules with L4T R32.x), the `.dtbo` files are populated into the `/boot` directory in the rootfs, and you could modify `/boot/extlinux/extlinux.conf` to add an `FDTOVERLAY` line to have one or more overlays applied at boot time. OE-Core's support for generating `extlinux.conf` content does not include support for `FDTOVERLAY` lines, so to make such a change you would have to work out a way to rewrite that file in a `bbappend`.
