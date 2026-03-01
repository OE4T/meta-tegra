As of 01 June 2024, the `scarthgap-l4t-r35.x` and `kirkstone` branches support JetPack 5.1.3/L4T R35.5.0.

# Changes from L4T R35.4.1/JetPack 5.1.2

See the release notes in the NVIDIA documentation for this release for information on new and updated features:
* [Jetson Linux R35.5.0 page](https://developer.nvidia.com/embedded/jetson-linux-r3550)
* [JetPack 5.1.3 page](https://developer.nvidia.com/embedded/jetpack-sdk-513)

## Machine changes

No new machines in this release.

## BSP changes

Updates in this release, as they apply to OE/Yocto builds, are mainly fixes and improvements to the existing boot firmware and low-level libraries.

Storage layouts (the `flash_*.xml` files) have been updated slightly over R35.4.1. If you have a custom storage layout you have derived from an earlier version of L4T, you should review the differences for any adjustments you may need to make.

### Kernel changes

The upstream base was updated to 5.10.192.

### UEFI changes

For secured devices, UEFI now authenticates its variables.  **This requires the addition of an authentication key to the EKB,** without which your secured device will not boot.  This also means that an OTA update from an earlier R35.x release will require that your OTA package also update the EKB, so be warned.

For more information , see [this thread on the developer forum](https://forums.developer.nvidia.com/t/jetpack-5-1-3-boot-error-with-all-security-enabled/284400).

## JetPack changes

* VPI updated to version 2.4.8

# Other Notes

* While the NVIDIA release notes mention use of the grub loader in place of L4TLauncher for loading the OS from the UEFI bootloader, this has not been implemented or tested in the layer.  Likewise for PXE booting.