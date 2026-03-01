As of 10 Aug 2025, the `master` and `scarthgap` branches support JetPack 6.2.1/L4T R36.4.4.

# Changes from L4T R36.4.3/JetPack 6.2

See the release notes in the NVIDIA documentation for this release for information on new and updated features:
* [Jetson Linux R36.4.4 page](https://developer.nvidia.com/embedded/jetson-linux-r3644)
* [JetPack 6.2.1 page](https://developer.nvidia.com/embedded/jetpack-sdk-621)


## BSP changes

Support has been added for using a Hardware Security Module (HSM) for signing the boot firmware. In the layer, our `tegra-flash-helper` script has been updated to support an `--hsm` option, which gets passed through to the NVIDIA signing scripts for this feature.

Otherwise, this release mainly contains bugfixes. 

See NVIDIA release notes for information on other changes/improvements.

### Kernel changes

The Linux kernel remains at 5.15.148.

## JetPack changes

No major updates to the JetPack SDK.

## DeepStream SDK

No update to the DeepStream SDK. Version 7.1 remains compatible with JetPack 6.2.1.
