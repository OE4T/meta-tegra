For many L4T/Jetson Linux releases, NVIDIA has provided a mechanism (the `jetson-io` scripts) for applying device tree overlays (`.dtbo` files) dynamically at runtime. For OE/Yocto-based builds, device trees are built from sources, so runtime application of DTB overlays is less of an issue. The `meta-tegra` layer does provide some mechanisms for applying DTB overlays, through some build-time variable settings.

# Build-time application of overlays

This mechanism is supported in the branches based on L4T R32.6.x through R35.x only. Overlays are applied to the device tree during the kernel build, directly modifying your kernel DTB. (For L4T R36 and later, the NVIDIA device trees are no longer provided in the kernel source tree.)

## Locating overlays
The exact list of overlays supplied by NVIDIA varies by target platform. You can find them by building the kernel recipe (`virtual/kernel` or `linux-tegra`) and examining its output under `${BUILDDIR}/work/tmp/${MACHINE}/linux-tegra`.

## Applying overlays
Set the `KERNEL_DEVICETREE_APPLY_OVERLAYS` variable to a blank-separate list of `.dtbo` file names to have those overlays applied during the kernel build. You can do this in your machine configuration file, or add it, for example, to the `local.conf` file in your build workspace.

### Example
For example, to configure a Jetson Xavier NX development kit for IMX477 and IMX219 cameras, you would add the following line to your `$BUILDDIR/conf/local.conf` file:

        KERNEL_DEVICETREE_APPLY_OVERLAYS:jetson-xavier-nx-devkit = "tegra194-p3668-all-p3509-0000-camera-imx477-imx219.dtbo"

## Other possible use cases
For U-Boot-based Jetsons (only supported on a subset of Jetson modules with L4T R32.x), the `.dtbo` files will get populated into the `/boot` directory in the rootfs, and you could modify the `/boot/extlinux/extlinux.conf` file to add an `FDTOVERLAY` line to have one or more overlays applied at boot time. Unfortunately, OE-Core's support for generating `extlinux.conf` content does not include support for `FDTOVERLAY` lines, so to make such a change you would have to work out a way to rewrite that file in a bbappend.

## For out-of-tree device trees

For L4T R36.x, the `nvidia-kernel-oot` recipe is the default device tree provider for the Jetson platforms. You can also set the `PREFERRED_PROVIDER_virtual/dtb` variable to point to a recipe for providing your own customized device tree. To apply overlays to these device trees, add `fdtoverlay` invocations to the compilation step via a `bbappend` (for `nvidia-kernel-oot`) or in your custom recipe.

### Example out-of-tree devicetree in tegra-demo-distro

See the tegra-demo-distro example at [meta-tegrademo/recipes-bsp/tegrademo-devicetree](https://github.com/OE4T/tegra-demo-distro/tree/master/layers/meta-tegrademo/recipes-bsp/tegrademo-devicetree) which shows how to modify a base devicetree from `nvidia-kernel-oot` to one specific to your hardware platform. This simple example just adds a single "compatible" line to your base devicetree.  To use this example:

1. Determine which devicetree is currently in use.  One way to do this is with `bitbake -e <your image>` and look at the value of `KERNEL_DEVICETREE`.
2. Determine whether there's an existing devicetree in the [meta-tegrademo/recipes-bsp/tegrademo-devicetree](https://github.com/OE4T/tegra-demo-distro/tree/master/layers/meta-tegrademo/recipes-bsp/tegrademo-devicetree) which uses your existing devictree as a base.  Current examples are:
  * `tegra234-p3768-0000+p3767-0005-oe4t.dts`: `jetson-orin-nano-devkit` or `jetson-orin-nano-devkit-nvme` builds on a p3768 (Orin Nano Devboard) carrier
  * `tegra234-p3768-0000+p3767-0000-oe4t.dts`: Nvidia Jetson Orin NX 16GB in a p3768 (Orin Nano Devboard) carrier
  * `tegra234-p3737-0000+p3701-0000-oe4t.dts`: `jetson-agx-orin-devkit`
3. If there's not an existing devicetree built from your base `KERNEL_DEVICETREE`, follow the examples to add one to SRC_URI and to the repo.
4. Modify your MACHINE conf or local conf to specify your dtb provider and `KERNEL_DEVICETREE` using something like this:
```
PREFERRED_PROVIDER_virtual/dtb = "tegrademo-devicetree"
KERNEL_DEVICETREE:jetson-orin-nano-devkit-nvme = "tegra234-p3768-0000+p3767-0005-oe4t.dtb"
KERNEL_DEVICETREE:jetson-orin-nano-devkit = "tegra234-p3768-0000+p3767-0005-oe4t.dtb"
```
Where `KERNEL_DEVICETREE` overrides the setting for your MACHINE, referencing the devicetree filename with `*.dtb` in the place of `*.dts`.
5. Build, flash, and boot the board, and `cat /sys/firmware/devicetree/base/compatible` to see the compatible string printed as configured in the devicetree. You should see a string which starts with "oe4t", as shown here for the orin nano
```
root@jetson-orin-nano-devkit-nvme:~# cat /sys/firmware/devicetree/base/compatible
oe4t,p3768-0000+p3767-0005+tegrademonvidia,p3768-0000+p3767-0005-supernvidia,p3767-0005nvidia,tegra234
```

# Runtime application of overlays in SPI Flash

This mechanism is supported in branches based on L4T R35.x and later. Overlays are appended to the kernel DTB by the NVIDIA flashing/signing tools, and are applied by the UEFI bootloader at runtime.  Overlays are stored in SPI flash and are only updated on capsule update or tegraflash.

## Locating overlays
The exact list of overlays supplied by NVIDIA varies by target platform. You can find them on R35.x-based branches by building the kernel recipe (`virtual/kernel` or `linux-tegra`) and examining its output under `${BUILDDIR}/work/tmp/${MACHINE}/linux-tegra`.  For R36.x-based branches, device trees are built as part of the `nvidia-kernel-oot` recipe.

## Applying overlays
Append your additional overlays to the `TEGRA_PLUGIN_MANAGER_OVERLAYS` variable, which consists of a blank-separate list of `.dtbo` file names. You can do this in your machine configuration file, or add it, for example, to the `local.conf` file in your build workspace. That variable is set by the layer to include overlays that NVIDIA requires for its platforms, so be sure to append to it, rather than overwriting it.

### Example
For example, to configure the pins on the 40-pin expansion header of the Jetson Orin Nano development kit, you would add the following line to your `$BUILDDIR/conf/local.conf` file:

        TEGRA_PLUGIN_MANAGER_OVERLAYS:append:jetson-orin-nano-devkit = " tegra234-p3767-0000+p3509-a02-hdr40.dtbo"


# Runtime application of overlays in the rootfs partition

With https://github.com/OE4T/meta-tegra/pull/1968 support is available to apply overlays in the rootfs partition using the `OVERLAYS` extlinux.conf option.  This means you are able to link overlays to a rootfs slot and store/update there instead of in the SPI flash.

Only overlays which modify the kernel DTB are supported, since the overlay application happens late in the boot sequence.

See [this section](https://github.com/OE4T/meta-tegra/wiki/extlinux.conf-support#uboot_extlinux_fdtoverlays) of the extlinux.conf wiki page for details about configuring `OVERLAYS` in extlinux.conf.