As of 08 Dec 2023, this feature is supported in the kirkstone, mickledore, nanbield, and master branches.

As of the latest Jetpack 5 r35.x releases, NVIDIA provides partition layouts which support [Root File System Redundancy](https://docs.nvidia.com/jetson/archives/r35.4.1/DeveloperGuide/text/SD/RootFileSystem.html#root-file-system-redundancy), whereby bootloader slots and rootfs slots are paired together to support automatically selecting the associated root filesystem partition at boot to match the selected bootloader slot.  The selected bootloader slot, a or b, will select the corresponding rootfs slot a or b.

When paired with the UEFI [capsule update](https://github.com/OE4T/meta-tegra/pull/1285) feature, a redundant root filesystem supports switching the root filesystem, kernel, and kernel dtb to match the updated bootloader slot.  When paired with an update tool which can update kernel, dtb and rootfs partitions (swupdate, rauc, mender, or others) the process of performing capsule update can also switch to an updated rootfs through the redundant rootfs feature.

If you have the available root filesystem space to support redundant rootfs, using a redundant partition layout at the outset of your project might give you the option to support updates later without a repartition (or tegraflash) of the device.

# Selecting Redundant Root Filesystem Partition Layout

By default, both the stock NVIDIA provided Jetpack image as well as OE4T images use the non redundant partition layouts.

To use NVIDIA provided redundant partition layouts and automatically apply the necessary [dtb changes performed by NVIDIA's flash.sh script](todo), on branches which include https://github.com/OE4T/meta-tegra/pull/1428, you simply need to set `USE_REDUNDANT_FLASH_LAYOUT_DEFAULT = "1"` in your distro configuration, custom MACHINE configuration, (or local.conf).  This is currently supported for most targets.  See the notes below for limitations.

This configuration is set as the default for all supported targets when building with [tegra-demo-distro](https://github.com/OE4T/tegra-demo-distro).

# Testing Root Filesystem A/B Slot Switching

See the sequence in https://github.com/OE4T/meta-tegra/pull/1428 to validate root slot and boot slot switching.

# Setting Up a Custom MACHINE

Use these variables to setup a MACHINE or distro with support for redundant flash layouts:
* `USE_REDUNDANT_FLASH_LAYOUT_DEFAULT` - Set to `"1"` in your distro layer to use redundant flash layouts for any supported MACHINEs.  Set to `"0"` to use default non-redundant layouts from NVIDIA when using tegra-demo-distro (`USE_REDUNDANT_FLASH_LAYOUT_DEFAULT` is the default for master branch builds of tegra-demo-distro).
* `ROOTFSPART_SIZE_DEFAULT` - Set with the size of the root filesystem partition when using the default (non-redundant) flash layout.  This size will be automatically divided by 2 when `USE_REDUNDANT_FLASH_LAYOUT` is selected.
* `PARTITION_LAYOUT_TEMPLATE_DEFAULT` - set with the partition layout to use with the default (non, external, non redundant) flash layout, for instance `custom_layout.xml`.   Either provide a `custom_external_layout_rootfs_ab.xml` file or define `PARTITION_LAYOUT_TEMPLATE_REDUNDANT` with your redundant file.
* `PARTITION_LAYOUT_TEMPLATE_DEFAULT_SUPPORTS_REDUNDANT` - Set to `"1"` if no `PARTITION_LAYOUT_TEMPLATE_REDUNDANT` is required for this MACHINE (and the same template is used for redundant or non redundant builds).
* `PARTITION_LAYOUT_EXTERNAL_DEFAULT` - Set with the default partition layout when using an external device (sdcard or NVMe) for rootfs partition storage, for instance `custom_external_layout.xml`. Either provide a `custom_external_layout_rootfs_ab.xml` file or define `PARTITION_LAYOUT_EXTERNAL_REDUNDANT` with your redundant file.
* `HAS_REDUNDANT_PARTITION_LAYOUT_EXTERNAL` - Set to `"0"` if your MACHINE does not support a `PARTITION_LAYOUT_EXTERNAL_REDUNDANT` and therefore does not support `USE_REDUNDANT_FLASH_LAYOUT_DEFAULT` 

## Overriding BSP Layer Changes

Use `ROOTFSPART_SIZE`, `PARTITION_LAYOUT_EXTERNAL` and `PARTITION_LAYOUT_TEMPLATE` as done before changes in https://github.com/OE4T/meta-tegra/pull/1428, to provide your own implementation outside the BSP layer and ignore the setting of `USE_REDUNDANT_FLASH_LAYOUT`.

# Limitations

NVIDIA does not provide a redundant flash layout for `flash_l4t_external.xml`.  Any targets which use `flash_l4t_external.xml`, which as of   https://github.com/OE4T/meta-tegra/pull/1295 include Orin NX 16 GB in P3509 carrier, Orin NX 16 GB in P3768 carrier, or Orin Nano 4GB in p3768 carrier use `HAS_REDUNDANT_PARTITION_LAYOUT_EXTERNAL ?= "0"` and therefore don't support the `USE_REDUNDANT_FLASH_LAYOUT` feature described here.  Alternatively, override `USE_REDUNDANT_FLASH_LAYOUT = "1"` and set `PARTITION_LAYOUT_EXTERNAL_DEFAULT ?= "flash_l4t_nvme.xml"` or your custom external layout, but be aware of issue https://github.com/OE4T/meta-tegra/discussions/1286.
`
