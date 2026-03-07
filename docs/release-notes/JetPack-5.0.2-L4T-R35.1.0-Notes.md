As of 18 Sep 2022, the `master` and `kirkstone` branches support Jetson Linux R35.1.0 and JetPack 5.0.2 (rev. 1). 

Please note that the official name for the BSP from NVIDIA is **Jetson Linux**, instead of Linux for Tegra.  We'll continue to use "L4T" as an abbreviation.

# Changes from L4T R32.7.2/JetPack 4.6.2

This is a major update to L4T and JetPack which adds support for the Jetson AGX Orin modules and _removes_ support for **all other** Jetson modules except for the Jetson AGX Xavier and Jetson Xavier NX series.

NVIDIA documentation:
* [Jetson Linux R35.1.0 page](https://developer.nvidia.com/embedded/jetson-linux-r351)
* [JetPack 5.0.2 page](https://developer.nvidia.com/embedded/jetpack-sdk-502)

## Machine changes

The **only** Jetson modules supported in this release (and future releases) are:
* Jetson AGX Orin series
* Jetson AGX Xavier series
* Jetson Xavier NX series

We also have a machine configuration for the Clara AGX development kit.

## BSP changes

This is a **major** update to the BSP and is not compatible with the R32.x series of releases.

* The `cboot` bootloader has been replaced by UEFI. You can use either NVIDIA's prebuilt copy, or build it from source.
* Boot logos/splash screens are now built into the UEFI bootloader, rather than being separately loaded.  Recipes for custom boot logos have been removed from the layer.
* The device tree plugin manager, which was a `cboot` feature, is no longer supported.  To dynamically modify the device tree (e.g., for camera configuration), you must configure a device tree overlay instead.
* Bootloader updating and redundancy behaves differently than with earlier L4T releases.  See the L4T documentation for details.
* The Linux kernel has been updated to 5.10.104.  The repository used in the layer for this new kernel is [here](https://github.com/OE4T/linux-tegra-5.10).
* The `trusty` trusted OS has been replaced by OP-TEE.
* Open-source display driver (AGX Orin series only).
* The OpenMAX gstreamer plugin (`gstreamer1.0-omx-tegra`), which has been deprecated since L4T R32.1.0, is no longer supported.
* Improved support for Wayland/Weston via the EGL-GBM backend interface.
* Container support now defaults to a smaller set of libraries passed through from the host to the container.

### Kernel changes
Besides the upgrade to the Linux 5.10 LTS kernel as a base, the new default kernel configuration for Jetson devices
builds far more features and drivers as modules, rather than building them into the kernel itself. If you have recipes
that depend on specific kernel features/drivers, you may need to add `RRECOMMENDS` settings to ensure that the
necessary modules get installed into your rootfs image.  The kernel configuration in the layer diverges slightly from the stock
Jetson Linux configuration by building in a small number of drivers, rather than leaving them as modules.

## JetPack changes

* CUDA updated to 11.4.
* New versions of cuDNN, TensorRT, VPI.
* `libvisionworks` is no longer supported.

## DeepStream SDK

With JetPack 5.0.2 (rev. 1), the DeepStream SDK has been updated to version 6.1.1-1.  The recipes in `meta-tegra-community` have
been updated accordingly for the SDK itself and the Python bindings.

# Known Issues
There are some known issues with the JetPack 5 integration.  See [this issues list](https://github.com/OE4T/meta-tegra/issues?q=is%3Aissue+is%3Aopen+milestone%3A%22JetPack+5%22) for the current status.  Any help with resolving these issues would be appreciated!

In addition, while NVIDIA supplies sources for the OP-TEE trusted OS, recipes for building OP-TEE from source are not yet ready for merging.
