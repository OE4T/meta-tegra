# Using device tree overlays (and custom devicetrees)

For many L4T/Jetson Linux releases, NVIDIA has provided a mechanism (the `jetson-io` scripts) for applying device tree overlays (`.dtbo` files) dynamically at runtime. For OE/Yocto-based builds, device trees are built from sources, so runtime application of DTB overlays is less of an issue. The `meta-tegra` layer does provide some mechanisms for applying DTB overlays, through some build-time variable settings. Creating the devicetree and devicetree overlay sources is done with recipes providing either `virtual/dtb` or `virtual/dtbo`, respectively.

## For out-of-tree devicetrees (virtual/dtb)

The `nvidia-kernel-oot-dtb` recipe is the default device tree provider for the devkit machines in OE4T, which builds devicetree binaries from the L4T sources packaged by `nvidia-kernel-oot`. You can also set the `PREFERRED_PROVIDER_virtual/dtb` variable to point to a recipe for providing your own customized device tree, and inherit the same `tegra-devicetree` bbclass used by the `nvidia-kernel-oot-dtb` recipe.

### Example out-of-tree devicetree in tegra-demo-distro

See the tegra-demo-distro example at [meta-tegrademo/recipes-bsp/tegrademo-devicetree](https://github.com/OE4T/tegra-demo-distro/tree/master/layers/meta-tegrademo/recipes-bsp/tegrademo-devicetree) which shows how to modify a base devicetree from `nvidia-kernel-oot-dtb` to one specific to your hardware platform. This simple example just adds a single "compatible" line to your base devicetree.  To use this example:

1. Determine which devicetree is currently in use.  One way to do this is with `bitbake -e <your image>` and look at the value of `KERNEL_DEVICETREE`.
2. Determine whether there's an existing devicetree in the [meta-tegrademo/recipes-bsp/tegrademo-devicetree](https://github.com/OE4T/tegra-demo-distro/tree/master/layers/meta-tegrademo/recipes-bsp/tegrademo-devicetree) which uses your existing devictree as a base.  Current examples are:
  * `tegra234-p3768-0000+p3767-0005-oe4t.dts`: `jetson-orin-nano-devkit` or `jetson-orin-nano-devkit-nvme` builds on a p3768 (Orin Nano Devboard) carrier
  * `tegra234-p3768-0000+p3767-0000-oe4t.dts`: Nvidia Jetson Orin NX 16GB in a p3768 (Orin Nano Devboard) carrier
  * `tegra234-p3737-0000+p3701-0000-oe4t.dts`: `jetson-agx-orin-devkit`
  * `tegra264-p4071-0000+p3834-0008-oe4t.dts`: `jetson-agx-thor-devkit` (AGX Thor T5000, p3834-0008 module)
  * `tegra264-p4071-0000+p3834-0000-oe4t.dts`: `jetson-agx-thor-t4000` (AGX Thor T4000, p3834-0000 module)
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

## For out-of-tree overlays (virtual/dtbo)

Overlay files can also be built from source via a `virtual/dtbo` provider, analogous to `virtual/dtb` for device trees. The default provider is `nvidia-kernel-oot-dtbo`, which builds NVIDIA's platform overlays from the `nvidia-kernel-oot` source tree.

To provide custom overlays built from source, create a recipe structured like `tegrademo-devicetree` but with `PROVIDES = "virtual/dtbo"` added and overlay source files in `SRC_URI`:

```bitbake
inherit tegra-devicetree

PROVIDES = "virtual/dtbo"
COMPATIBLE_MACHINE = "(tegra)"

S = "${UNPACKDIR}"

SRC_URI = "file://my-board-overlay.dtso"
```

Then point to it in your machine or distro configuration:

```
PREFERRED_PROVIDER_virtual/dtbo = "my-overlay-recipe"
```

The `tegra-devicetree` class installs built DTBO files to `/boot/devicetree/` in the sysroot. The `l4t-launcher-extlinux` recipe picks up all `.dtbo` files from that directory. Any files not explicitly named in `UBOOT_EXTLINUX_FDTOVERLAYS` or `L4T_UBOOT_EXTLINUX_EXTRA_FDTS` are placed in the `l4t-launcher-extlinux-dtb-extra` package. See [extlinux.conf-support.md](extlinux.conf-support.md) for details.

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

With [https://github.com/OE4T/meta-tegra/pull/1968](https://github.com/OE4T/meta-tegra/pull/1968) support is available to apply overlays in the rootfs partition using the `OVERLAYS` extlinux.conf option.  This means you are able to link overlays to a rootfs slot and store/update there instead of in the SPI flash.

Only overlays which modify the kernel DTB are supported, since the overlay application happens late in the boot sequence.

See [this section](extlinux.conf-support.md#uboot_extlinux_fdtoverlays) of the extlinux.conf wiki page for details about configuring `OVERLAYS` in extlinux.conf.