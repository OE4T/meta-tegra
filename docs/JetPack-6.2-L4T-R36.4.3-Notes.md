As of 02 May 2025, the `master`, `walnascar`, and `scarthgap` branches support JetPack 6.2/L4T R36.4.3.

# Changes from L4T R36.4.0/JetPack 6.1

See the release notes in the NVIDIA documentation for this release for information on new and updated features:
* [Jetson Linux R36.4.3 page](https://developer.nvidia.com/embedded/jetson-linux-r3643)
* [JetPack 6.2 page](https://developer.nvidia.com/embedded/jetpack-sdk-62)


## BSP changes

* The main new feature in this release is the addition of "super" power modes for the P3767 series modules (Orin NX and Orin Nano).
* The `JetsonMinimal` UEFI build configuration, instead of full UEFI, is now used for RCM booting. (RCM boot is used for initrd-based flashing.)

See NVIDIA release notes for information on other changes/improvements.

### Kernel changes

The Linux kernel remains at 5.15.146.

## JetPack changes

No major updates to the JetPack SDK.

## DeepStream SDK

No update to the DeepStream SDK. Version 7.1 remains compatible with JetPack 6.2.
