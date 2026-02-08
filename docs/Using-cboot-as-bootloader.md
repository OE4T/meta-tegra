[Applicable to L4T R32.1.0 and later]

For Jetson AGX Xavier, NVIDIA provides only **cboot** as the bootloader, so there is no U-Boot recipe for that platform. For Jetson TX2, the default configuration uses both - cboot loads U-Boot, which then loads the Linux kernel. You can, however, use just cboot as the bootloader by setting
```
PREFERRED_PROVIDER_virtual/bootloader = "cboot-prebuilt"
```
in your build configuration. If you do this, cboot directly loads the Linux kernel and initial ramdisk from the `kernel` (or `kernel_b`) partition, and the kernel image is not added to the root filesystem.

For branches with L4T R32.4.3 and later (`dunfell-l4t-r32.4.3`, `gatesgarth` and later branches), cboot is now built from sources by default, rather than using the prebuilt copy that comes with the L4T kit, so you should specify `cboot-t18x` instead of `cboot-prebuilt` for the PREFERRED_PROVIDER setting.

*Note* that in L4T R32.2.x, cboot has issues if the kernel or the initrd is too large, at least on TX2 platforms,
causing kernel panics at boot time. With L4T R32.3.1, the kernel size limitation appears to be resolved, but
if you use a separate initrd (instead of building it into the kernel as an initramfs), there is still a limit
of just a few megabytes on its size (the relevant definitions (for the TX2) are probably in bootloader/partner/t18x/common/include/soc/t186/tegrabl_sdram_usage.h in the cboot sources). If you plan to customize your kernel to build in more drivers, rather than
leaving them as loadable modules, or if you need to build more functionality into your initial ram filesystem,
use R32.3.1 and bundle the initramfs into your kernel.

## Building cboot from sources
NVIDIA has, from time to time, made cboot source code available.  For Jetson AGX Xavier platforms, the most recent source release was with L4T R32.2.3, published in the L4T `public_sources` archive. This copy of cboot was removed from L4T R32.3.1.  For L4T R32.4.2, cboot sources have been published again (for Xavier platforms only) as a separate download. For L4T R32.4.3 and R32.4.4, cboot sources are available for both TX2 and Xavier platforms.

Older releases (R28.x for TX2, R31.1 for Xavier) were restricted downloads.  You must use your Developer Network login credentials to download the source package from the appropriate L4T page on NVIDIA's website and store that tarball on your build host. The NVIDIA_DEVNET_MIRROR variable is used to locate the sources; see the recipes for more details on naming.

To use cboot built from source in your pre-R32.4.3 builds, set
```
PREFERRED_PROVIDER_virtual/bootloader = "cboot"
```
For R32.4.3 and later, the default is to build cboot from source, and the recipe names changed to be `cboot-t18x` for Jetson TX2 platforms and `cboot-t19x` for Jetson Xavier platforms.

## PACKAGECONFIG for cboot builds
In branches with L4T R32.4.3 and later, you can control the inclusion of some cboot features by modifying the PACKAGECONFIG setting for the cboot recipe for your target device. All features are enabled by default, to match the stock L4T settings.

For Jetson-TX2 (tegra186/t18x) platforms, the following PACKAGECONFIG options are available:
| PACKAGECONFIG option | Description                                                                     |
|----------------------|---------------------------------------------------------------------------------|
| display              | cboot initializes the display; can be disabled for headless targets             |
| recovery             | enables booting the recovery kernel and rootfs (not currently populated in L4T) |

For Xavier (tegra194/t19x) platforms, the following PACKAGECONFIG options are available:
| PACKAGECONFIG option | Description                                                                     |
|----------------------|---------------------------------------------------------------------------------|
| bootdev-select       | enables booting from devices other than the built-in eMMC or SATA interfaces    |
| display              | cboot initializes the display; can be disabled for headless targets             |
| ethernet             | enables booting over the Ethernet interface                                     |
| extlinux             | enables cboot's half-baked support for using an `extlinux.conf` file            |
| recovery             | enables booting the recovery kernel and rootfs (not currently populated in L4T) |
| shell                | enables the countdown pause during boot to break into the cboot "shell"         |

Note that removing the `bootdev-select` option has no effect on builds for the Xavier NX development kit; the recipe always enables that option for that target, since it is required for booting from the SDcard.

# Jetson TX1/Nano platforms
While NVIDIA does ship a pre-built version of cboot for the tegra210 platforms (TX1 and Nano), they do not provide source code.  U-Boot is the user-modifiable bootloader for those platforms.