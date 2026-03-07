As of 28 Feb 2026, the `master` and `scarthgap` branches support JetPack 6.2.2/L4T R36.5.0.

# Changes from L4T R36.4.4/JetPack 6.2.1

See the release notes in the NVIDIA documentation for this release for information on new and updated features:
* [Jetson Linux R36.5.0 page](https://developer.nvidia.com/embedded/jetson-linux-r365)
* [JetPack 6.2.2 page](https://developer.nvidia.com/embedded/jetpack-sdk-622)

## BSP changes

This release mainly contains bugfixes. 

See NVIDIA release notes for information on other changes/improvements.

### Kernel changes

The Linux kernel (`linux-jammy-nvidia-tegra`) was updated to 5.15.185.

The NVIDIA out-of-tree drivers have been patched so they build with the NVIDIA-provided kernel as well as newer `linux-yocto` kernels (6.6 for `scarthgap` and 6.18 for `master`).

## JetPack changes

No major updates to the JetPack SDK.

## DeepStream SDK

No update to the DeepStream SDK. Version 7.1 remains compatible with JetPack 6.2.2.
