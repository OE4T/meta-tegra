The `meta-tegra` layer includes MACHINE definitions for NVIDIA's Jetson development kits. If you are developing a custom device using one of the Jetson modules with, for example, a custom carrier board, or you just want to modify the default boot-time configuration (pinmux, etc.) for an existing development kit as a separate MACHINE in your own metadata layer, you may need to supply a MACHINE-specific file for your builds.

**IMPORTANT:** For any custom carrier board/hardware design, make sure you consult the appropriate Platform Adaptation and Bring-Up Guide document available from the [NVIDIA Developer Download site](https://developer.nvidia.com/embedded/downloads) to get all the details on how to customize the pinmux configuration and other low-level hardware configuration settings. _Failing to provide the correct settings could damage your device._

Boot-time hardware configuration and boot flash programming is particularly complicated for Jetson modules, and varies substantially between models. Consult a recent version of the [L4T Driver Package Documentation](https://docs.nvidia.com/jetson/archives/l4t-archived/l4t-322/index.html), particularly the "BSP Customization" and "Bootloader" chapters, for background information.  As mentioned above, the Platform Adaptation documentation is also a good reference.

**NOTE:** Due to restrictions in the implementation of bootloader update payloads, the length of your custom MACHINE name should
be 31 characters or less.

# Jetson-TX1 #
No additional build-time files are necessary for MACHINEs based on the Jetson-TX1 module. All customizations can be done in the device tree and/or U-Boot.  You'll need to point your build at your customized kernel and/or U-Boot repository and set variables in the machine `.conf` file for your custom device.

# Jetson-Nano #
In the `warrior` and `zeus` branches, the only MACHINE-specific build-time file for Jetson-Nano is the SDCard layout file used by `recipes-bsp/sdcard-layout/sdcard-layout_1.0.bb`.  If you modify the partition layout for the SDCard, you'll need to supply a copy of the `sdcard-layout.in` file that matches the SDCard partitions you define in your customized version of the `flash_l4t_t210_spi_sd_p3448.xml` file from the L4T BSP.

Starting with the `zeus-l4t-r32.3.1` branch, full support for all revisions and SKUs of the Jetson Nano module was added, and the SDcard layout file was eliminated.  To modify your partition layout, you need only provide a customized copy of the `flash_l4t_t210_spi_sd_p3448.xml` (for 0000 SKUs) or `flash_l4t_t210_emmc_p3448.xml` (for 0002 SKUs) file. Different module revisions (FABs) use different device tree files, so you may need to have multiple device tree source files to account for module variants in your custom device/carrier.

# Jetson-TX2 and Jetson-TX2i #

For the Jetson-TX2 family, there are several boot-time configuration files that are machine-specific. Be sure to follow the Platform Adaptation Guide documentation carefully so all of the necessary customizations for the BPMP device tree and the MB1 `.cfg` files for the pinmux, PMIC, PMC, boot ROM, and other on-module hardware get created properly.  The basic steps are filling in the pinmux spreadsheet and generating the `dtsi` fragments, then converting those fragments to `cfg` files using the L4T `pinmux-dts2cfg.py` script.

The `recipes-bsp/tegra-binaries/tegra-flashvars_<bsp-version>.bb` recipe installs a file called `flashvars` that identifies the boot-time configuration files that need to be processed by the `tegra186-flash-helper` script for feeding into NVIDIA's flashing tools.  With older OE4T branches, you need to supply a customized copy of the `flashvars` file in your BSP layer.  With the latest branches, the `flashvars` file gets generated automatically from the variables listed in `TEGRA_FLASHVARS`.  Check the recipe in `meta-tegra` to confirm which method you need to follow.

The files listed in your `flashvars` file must be installed into `${datadir}/tegraflash` in the build sysroot by another recipe.  The simplest method is to create an overlay for the `recipes-bsp/tegra-binaries/tegra-bootfiles` recipe, as it already extracts the files for the Jetson development kits from the L4T BSP package:
```
# The fetch task is disabled on this recipe, but we need our files included in the task signature.
CUSTOM_DTSI_DIR := "${THISDIR}/${BPN}"
FILESEXTRAPATHS:prepend := "${CUSTOM_DTSI_DIR}:"

SRC_URI:append:${machine} = "\
    file://tegra19x-${machine}-padvoltage-default.cfg \
    file://tegra19x-${machine}-pinmux.cfg \
    "

# As the fetch task is disabled for this recipe, we access the files directly out of the layer.
do_install:append:${machine}() {
    install -m 0644 ${CUSTOM_DTSI_DIR}/tegra19x-${machine}-padvoltage-default.cfg ${D}${datadir}/tegraflash/
    install -m 0644 ${CUSTOM_DTSI_DIR}/tegra19x-${machine}-pinmux.cfg ${D}${datadir}/tegraflash/
}
```

The specifics of the configuration files and variables required may vary from version to version of the L4T BSP, so be sure to review any changes when upgrading.

# Jetson AGX Xavier #
Jetson AGX Xavier systems are similar to Jetson-TX2, but (as of this writing) have only two version-dependent boot-time files - the BPMP device tree and the PMIC configuration.  Consult the NVIDIA documentation for customization steps, and see the Jetson-TX2 section above for information on how to integrate your custom files into the build.

Note that AGX Xavier targets handle UEFI variables differently than other platforms.  If you plan to use with Jetpack 5 branches, please read [https://github.com/OE4T/meta-tegra/pull/1865](https://github.com/OE4T/meta-tegra/pull/1865) and note that you likely will want to define `TNSPEC_COMPAT_MACHINE`.

# Jetson Xavier NX
Jetson Xavier NX systems are similar to Jetson AGX Xavier, but (as of this writing) have no version-dependent
boot-time files.  Consult the NVIDIA documentation for customization steps, and see the Jetson-TX2 section above
for information on how to integrate your customized files into the build.

# Jetson Orin
This guide is based on Jetson Linux R35.4.1 so change bbappend names accordingly if you use a different release. Occurences of `${machine}` should be replaced by your machine name.

## Create a new machine config
Create a new Machine configuration at `conf/machine/${machine}.conf` in your layer.
For guidance on what it should contain look at any of the machine configurations in `meta-tegra`.

## Create a new flash config
Create a new flash configuration `recipes-bsp/tegra-binaries/tegra-flashvars/${machine}/flashvars`. You can start by copying one of the `flashvars` files in `meta-tegra`.
To use the newly created `flashvars` file create the following `recipes-bsp/tegra-binaries/tegra-flashvars_35.4.1.bbappend`:
```
FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"
```

## Add pinmux dtsi files
Generate the pinmux dtsi files with the [Nvidia pinmux Excel sheet](https://developer.nvidia.com/downloads/jetson-orin-nx-and-orin-nano-series-pinmux-config-template) (or [this one for Orin AGX](https://developer.nvidia.com/embedded/secure/jetson/agx_orin/jetson_agx_orin_pinmux_config_template.xlsm)).
Rename the resulting files to start with `tegra234-` (Otherwise `meta-tegra` has issues handling them.) and convert line endings to Unix using `dos2unix`. Copy the files to `recipes-bsp/tegra-binaries/tegra-flashvars`.

**NOTE:** If you manually rename your generated DTSI files, you may need to modify the `#include` statement on line 35 of your `-pinmux.dtsi` file, as it has the original filename for the `-gpio-default.dtsi` file hardcoded.

Install the files with following `tegra-bootfiles_35.4.1.bbappend`:
```
# Hack: The fetch task is disabled on this recipe, so the following is just for the task signature.
FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"
SRC_URI:append:${machine} = "\
    file://tegra234-${machine}-gpio-default.dtsi \
    file://tegra234-${machine}-padvoltage-default.dtsi \
    file://tegra234-${machine}-pinmux.dtsi \
"

# Hack: As the fetch task is disabled for this recipe, we have to directly access the files."
CUSTOM_DTSI_DIR := "${THISDIR}/${BPN}"
do_install:append:${machine}() {
    install -m 0644 ${CUSTOM_DTSI_DIR}/tegra234-${machine}-gpio-default.dtsi ${D}${datadir}/tegraflash/
    install -m 0644 ${CUSTOM_DTSI_DIR}/tegra234-${machine}-padvoltage-default.dtsi ${D}${datadir}/tegraflash/
    install -m 0644 ${CUSTOM_DTSI_DIR}/tegra234-${machine}-pinmux.dtsi ${D}${datadir}/tegraflash/
}
```
(Don't forget to replace `${machine}` with your machine name.)

Then modify `flashvars` to use the files:
- `PINMUX_CONFIG` should be set to your `tegra234-${machine}-pinmux.dtsi`
- `PMC_CONFIG` should be set to your `tegra234-${machine}-padvoltage-default.dtsi`

## (Optionally) disable board EEPROM usage
As explained in the _Platform Adaptation and Bring-Up Guide_ by Nvidia, you might want to disable the usage of the board EEPROM.
For that create a copy of the file used in `flashvars` for `MB2BCT_CFG` and modify it according to the Nvidia guide.
Include this new file in Yocto the same way as explained in [Add pinmux dtsi files](#add-pinmux-dtsi-files) and update `MB2BCT_CFG` in `flashvars` with the new file name.

## Use a custom device tree
See [Custom Device Tree](#custom-device-tree) and apply the described changes to your `${machine}.conf`.

# Customizing the kernel
For custom hardware, you'll probably need to modify the kernel in at least one of the following ways:
* Custom kernel configuration
* Custom device tree
* Adding patches

Starting with the L4T R32.3.1-based branches, you can use the [Yocto Linux tools](https://www.yoctoproject.org/docs/3.1/kernel-dev/kernel-dev.html) to apply patches and configuration
changes during the build, although it may be simpler to fork the [linux-tegra-4.9 repository](https://github.com/madisongh/linux-tegra-4.9) to apply patches, and supply your own `defconfig` file for
the kernel configuration.  Having your own fork of the kernel sources should also be easier for creating a custom device tree. (You should also set the KERNEL_DEVICETREE variable in your machine configuration file appropriately.)

# Custom MACHINE definitions for existing hardware #
If you need to define an alternate MACHINE configuration for an NVIDIA Jetson development kit without altering the boot-time configuration files for hardware initialization, you can have your MACHINE reuse the existing files in `meta-tegra`. For example, let's say you want to create `tegraflash` packages for the Jetson-TX2 development kit for both the default cboot->U-boot->Linux boot sequence as well as for booting directly from cboot to Linux, without U-Boot.  In your BSP or distro layer, you could add a machine configuration file called, for example, `conf/machine/jetson-tx2-cboot.conf` that looks like this:
```
MACHINEOVERRIDES = "jetson-tx2:${MACHINE}"
require conf/machine/jetson-tx2.conf
PACKAGE_EXTRA_ARCHS_append = " jetson-tx2"
PREFERRED_PROVIDER_virtual/bootloader = "cboot-prebuilt"
```
This would override the bootloader settings in the default `jetson-tx2` configuration to use cboot instead of U-Boot, but otherwise reuse all of the MACHINE-specific packages, files, and settings for the `jetson-tx2` MACHINE in `meta-tegra`.

For Jetson Xavier NX based machine types - `jetson-xavier-nx-devkit` and `jetson-xavier-nx-devkit-emmc`, the `conf/machine/custom-machine.conf` would look like this:
```
require conf/machine/jetson-xavier-nx-devkit-emmc.conf
MACHINEOVERRIDES = "cuda:tegra:tegra194:xavier-nx:jetson-xavier-nx-devkit-emmc:${MACHINE}"
PACKAGE_EXTRA_ARCHS_append = " jetson-xavier-nx-devkit-emmc"
```

# Custom Device Tree #

In many cases it is desirable to avoid forking or patching the kernel sources. The devicetree bbclass can be used to create a custom dtb. There's an example in tegra-demo-distro documented at [Using-device-tree-overlays](Using-device-tree-overlays.md#example-out-of-tree-devicetree-in-tegra-demo-distro) which accomplishes this for recent branches.

# Custom Partitioning #

See [Redundant-Rootfs-A-B-Partition-Support](Redundant-Rootfs-A-B-Partition-Support.md) for suggestions regarding defining partition layout files for your MACHINE.