As of 07 Jul 2025, the `kirkstone` and `scarthgap-l4t-r35.x` branches support JetPack 5.1.5/L4T R35.6.2.

This is a minor update to the L4T BSP only, with no JetPack changes.

# Changes from L4T R35.6.1/JetPack 5.1.5

See the release notes in the NVIDIA documentation for this release for information on changes, but essentially has only bug fixes over R35.6.2:
* [Jetson Linux R35.6.2 page](https://developer.nvidia.com/embedded/jetson-linux-r3562)
* [JetPack 5.1.5 page](https://developer.nvidia.com/embedded/jetpack-sdk-515) (same JetPack release as for L4T R35.6.1)

### Kernel changes

No kernel update in this release; the exact same kernel sources are used as for R35.6.1. However, the default `KERNEL_ARGS` setting for all machines has been changed to remove the `nospectre_bhb` parameter. If you have a custom machine configuration, you may wish to update your `KERNEL_ARGS` setting accordingly, to enable Spectre-BHB mitigations in the kernel.

### Other notes

While most of the BSP packages have been updated or re-issued with `35.6.2` version numbering, the Jetson Multimedia API `.deb` packages remain at version `35.6.1` in this release.