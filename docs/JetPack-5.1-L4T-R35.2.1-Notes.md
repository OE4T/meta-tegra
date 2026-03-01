As of 08 Feb 2023, the `master`, `langdale`, and `kirkstone` branches support Jetson Linux R35.2.1 and JetPack 5.1.
As of 16 Apr 2023, `master` and `kirkstone` have been updated to [JetPack 5.1.1/L4T R35.3.1](JetPack-5.1.1-L4T-R35.3.1-Notes.md).

# Changes from L4T R35.1.0/JetPack 5.0.2

See the release notes in the NVIDIA documentation for this release for information on new and updated features:
* [Jetson Linux R35.2.1 page](https://developer.nvidia.com/embedded/jetson-linux-r3521)
* [JetPack 5.1 page](https://developer.nvidia.com/embedded/jetpack-sdk-51)

## Machine changes

L4T R35.2.1 adds support for Jetson Orin NX 16GB production modules. A machine configuration for this module mounted on a Xavier NX development kit carrier board is available in the layer: [p3509-a02-p3767-0000.conf](https://github.com/OE4T/meta-tegra/blob/master/conf/machine/p3509-a02-p3767-0000.conf).


## BSP changes

Many of the BSP components have been updated.  The significant new feature in this release is support for [UEFI Secure Boot](Secure-Boot-Support-in-L4T-R35.2.1-and-later.md).

UEFI and OP-TEE are now built from source by default, rather than using the pre-built copies from the L4T package.

### Kernel changes

No major updates to the kernel, which is still based on 5.10.104.

## JetPack changes

* Minor updates to CUDA packages
* New versions of cuDNN, TensorRT, and VPI

## DeepStream SDK

With JetPack 5.1, the DeepStream SDK has been updated to version 6.2.0-1.  The recipes in `meta-tegra-community` have
been updated accordingly for the SDK itself and the Python bindings.

# Known Issues
There are some known issues with the JetPack 5 integration.  See [this issues list](https://github.com/OE4T/meta-tegra/issues?q=is%3Aissue+is%3Aopen+milestone%3A%22JetPack+5%22) for the current status.  Any help with resolving these issues would be appreciated!

NVIDIA issued an "overlay" to the L4T R35.2.1 kit to fix problems with Orin secure boot support (specifically, using SBKPKC signing + encryption).  That overlay was integrated in the `master` and `kirkstone` branches.

