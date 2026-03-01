As of 18 May 2025, the `kirkstone` and `scarthgap-l4t-r35.x` branches support JetPack 5.1.5/L4T R35.6.1.

# Changes from L4T R35.6.0/JetPack 5.1.4

See the release notes in the NVIDIA documentation for this release for information on new and updated features:
* [Jetson Linux R35.6.1 page](https://developer.nvidia.com/embedded/jetson-linux-r3561)
* [JetPack 5.1.5 page](https://developer.nvidia.com/embedded/jetpack-sdk-515)

## Machine changes

No new machines in this release. Existing machines for P3767 modules (Orin Nano and NX-based) have been updated to use the new MAXN_SUPER power model configurations, which affect the kernel device tree, BPMP configuration, and NVPMODEL configuration files.

## BSP changes

Other updates in this release are mainly fixes and improvements to the existing boot firmware and low-level libraries.

### Kernel changes

The upstream base remains at 5.10.216. Updates include some bug fixes and the device tree updates to support MAXN_SUPER power model configurations.