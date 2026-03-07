As of 01 June 2024, the `master` and `scarthgap` branches support JetPack 6.0/L4T R36.3.0.

# Changes from L4T R35.3.1/JetPack 5.1.1

See the release notes in the NVIDIA documentation for this release for information on new and updated features:
* [Jetson Linux R36.3.0 page](https://developer.nvidia.com/embedded/jetson-linux-r363)
* [JetPack 6.0 page](https://developer.nvidia.com/embedded/jetpack-sdk-60)

## Machine changes

L4T R36.x supports **only** Jetson Orin modules and development kits.  Support for Jetson Xavier modules has been removed.

## BSP changes

Updates in this release, as they apply to OE/Yocto builds, are mainly fixes and "improvements" to the existing boot firmware and low-level libraries.  This includes new versions of the secure OS (TF-A and OP-TEE) and the UEFI bootloader.

### Kernel changes

The 5.10+Android-based kernel has been replaced with a 5.15-based kernel from Ubuntu 22.04.  Jetson-specific drivers and device trees have been moved out of the kernel source tree and are now built separately. This change allows for the replacement of the NVIDIA-provided, Ubuntu-derived base kernel with other upstream kernels; see the L4T documentation and release notes for more information. 

## JetPack changes

Many of the JetPack packages have been updated, including CUDA (to 12.2).  See the JetPack release notes for more information.

## DeepStream SDK

The DeepStream SDK has been updated to version 7.0.

# Other Notes

* There are several known issues documented in the release notes, which are worth reviewing. In particular, USB connectivity during flashing can sometimes be a problem. Workarounds mentioned in the release notes include swapping cables and/or USB ports on your host PC, and rebooting your host PC if you encounter flashing failures.
* The NVIDIA-specific userland DRM library (`libdrm-nvdc`) has been removed in this release.
* Vulkan support is present, but you must manually include the `tegra-libraries-vulkan` package in your image to install the necessary configuration file for the Vulkan dispatcher. While NVIDIA now also supports VulkanSC in Jetson Linux, recipes to install those libraries are not yet available.
* Jetson-specific patches for wayland and weston are no longer needed.