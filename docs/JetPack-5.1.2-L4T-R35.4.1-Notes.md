As of 02 Sep 2023, the `master`, `mickledore`, and `kirkstone` branches support JetPack 5.1.2/L4T R35.4.1.

# Changes from L4T R35.3.1/JetPack 5.1.1

See the release notes in the NVIDIA documentation for this release for information on new and updated features:
* [Jetson Linux R35.4.1 page](https://developer.nvidia.com/embedded/jetson-linux-r3541)
* [JetPack 5.1.2 page](https://developer.nvidia.com/embedded/jetpack-sdk-512)

## Machine changes

L4T R35.4.1 adds support for the Jetson AGX Orin Industrial module. The machine configuration `jetson-agx-orin-devkit-industrial` has been added to the layer to build images for the module when installed in an AGX Orin development kit.

## BSP changes

Updates in this release, as they apply to OE/Yocto builds, are mainly fixes and improvements to the existing boot firmware and low-level libraries.

For multimedia support, the deprecated `nvbuf_utils` library was removed in this release.

### Kernel changes

No major updates.  The upstream base was updated to 5.10.120.

## JetPack changes

* VPI updated to version 2.3.9
* Nsight updated to 2023.2

## DeepStream SDK

The DeepStream SDK has been updated to version 6.3-1. The recipe in the [meta-tegra-community](https://github.com/OE4T/meta-tegra-community) repo has been updated.

# Other Notes

* While the NVIDIA release notes mention use of the grub loader in place of L4TLauncher for loading the OS from the UEFI bootloader, this has not been implemented or tested in the layer.  Likewise for PXE booting.