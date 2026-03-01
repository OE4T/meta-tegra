As of 16 Apr 2023, the `master`, `mickledore`, and `kirkstone` branches support JetPack 5.1.1/L4T R35.3.1.

# Changes from L4T R35.2.1/JetPack 5.1.0

See the release notes in the NVIDIA documentation for this release for information on new and updated features:
* [Jetson Linux R35.3.1 page](https://developer.nvidia.com/embedded/jetson-linux-r3531)
* [JetPack 5.1.1 page](https://developer.nvidia.com/embedded/jetpack-sdk-511)

## Machine changes

L4T R35.3.1 adds support for the Jetson Orin Nano developer kit. Machine configurations `jetson-orin-nano-devkit` and `jetson-orin-nano-devkit-nvme` have been added to the layer to build images for use of the kit with SDcard and NVMe storage, respectively.  These configurations should also be usable as a starting point for a custom machine based on one of the Orin Nano production modules.

## BSP changes

Other than the new hardware support, no major changes to the layer for the BSP update.  The secureboot overlay that NVIDIA issued to fix problems with Orin secure boot support (specifically, using SBKPKC signing + encryption) for R35.2.1 is also applied here.

### Kernel changes

There are no major updates to the kernel, which is still based on 5.10.104.

**NOTE,** however, that if you have a custom machine configuration based on one of the Xavier modules (tegra194), you should update your `KERNEL_ARGS` setting to add `video=efifb:off` as a kernel parameter to avoid system crashes at boot time.

## JetPack changes

* New versions of VPI and Nsight.

## DeepStream SDK

DeepStream SDK updates usually lag new JetPack releases.  Will update if/when a new version is released that supports JetPack 5.1.1.

# Other Notes

* The Orin Nano omits the hardware video encoder present in other Jetson models.  Don't try to use the NVIDIA-specific gstreamer plugins for video encoding on that platform; stick to the software-based plugins.

# Known Issues
* The `nvpmodel` may fail with an error on the first boot after flashing an Orin device.  You can clear this problem by using the `nvpmodel` command to select a power model configuration, then rebooting (changing the power model may prompt you to reboot immediately). **NOTE** Fixed with [PR #1294](https://github.com/OE4T/meta-tegra/pull/1294).
* Soft reboots on the Orin Nano devkit may fail during OP-TEE startup with a `Heap free list corrupted !!!` error.  Powering off the device for 10 seconds, then powering it back on, clears the problem. **NOTE** This does not occur when using the supported `initrd-flash` mechanism for flashing the development kit.  Do not try to use the direct flashing mechanism (the `doflash.sh` script) when flashing an Orin NX or Nano device using an NVMe drive for its rootfs storage.
* The `nvfancontrol` daemon on the Orin Nano devkit may raise the fan to the highest speed and log warnings about `Failed to open empty file! (Bad address)`.  This should also be fixed with [PR #1294](https://github.com/OE4T/meta-tegra/pull/1294).
* There are still some known issues outstanding with the JetPack 5 integration.  See [this issues list](https://github.com/OE4T/meta-tegra/issues?q=is%3Aissue+is%3Aopen+milestone%3A%22JetPack+5%22) for the current status.  Any help with resolving these issues would be appreciated!
