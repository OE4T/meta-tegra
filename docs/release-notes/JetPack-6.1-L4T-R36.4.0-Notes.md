As of 27 Oct 2024, the `master`, `styhead`, and `scarthgap` branches support JetPack 6.1/L4T R36.4.0.

# Changes from L4T R36.3.0/JetPack 6.0

See the release notes in the NVIDIA documentation for this release for information on new and updated features:
* [Jetson Linux R36.4.0 page](https://developer.nvidia.com/embedded/jetson-linux-r3640)
* [JetPack 6.1 page](https://developer.nvidia.com/embedded/jetpack-sdk-61)


## BSP changes

* NVIDIA has added an implementation of Firmware Trusted Platform Module (fTPM) as an OP-TEE TA. A recipe for building this has not yet been added to the layer.
* The default kernel arguments no longer set `net.ifnames=0`, so network interfaces will use the newer kernel naming convention.
* Source code is now provided for the `nvipcpipeline` and `nvunixfd` gstreamer plugins. Recipes have been added for these plugins, and they have been removed from the `gstreamer1.0-plugins-tegra-binaryonly` package.
* A minimal UEFI configuration is now supported on AGX Orin series modules. See [this section](https://docs.nvidia.com/jetson/archives/r36.4/DeveloperGuide/SD/Bootloader/UEFI.html#miniuefi-support) in the Jetson Linux Developer Guide for more information. The variable `TEGRA_UEFI_MINIMAL` can be set to `"1"` in your build configuration to use this configuration instead of the default.

See NVIDIA release notes for information on other changes/improvements.

### Kernel changes

The Linux kernel has been updated to 5.15.146.

## JetPack changes

Several of the JetPack packages have been updated, including CUDA (to 12.6).  See the JetPack release notes for more information.

## DeepStream SDK

DeepStream SDK 7.1 is compatible with JetPack 6.1. See the [meta-tegra-community work-in-progress branch](https://github.com/OE4T/meta-tegra-community/tree/wip-l4t-r36.4.0) for the updated recipes.
