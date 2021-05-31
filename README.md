OpenEmbedded/Yocto BSP layer for NVIDIA Jetson
==============================================

**NOTE:** This is an experimental branch for working with the pre-release UEFI
bootloader support for the Jetson AGX Xavier and Xavier NX platforms.

Linux4Tegra release: R32.4.4 with UEFI overlay
JetPack release:     4.4.1 (note that JetPack content is unlikely to work at all)

Boards supported:
* Jetson AGX Xavier development kit
* Jetson Xavier NX Development Kit

This layer depends on:
URI: git://git.openembedded.org/openembedded-core
branch: gatesgarth
LAYERSERIES_COMPAT: gatesgarth


PLEASE NOTE
-----------

* This branch uses the experimental UEFI bootloader (available from
  the [NVIDIA download center](https://developer.nvidia.com/embedded/downloads))
  and the Linux kernel version 5.12.8 from https://kernel.org. As such, any
  JetPack content that depends on the NVIDIA-provided downstream kernel (e.g.,
  CUDA, camera and video support, etc.) **will not work**.


Contributing
------------

Please see [CONTRIBUTING.md](CONTRIBUTING.md)

Contributions are welcome!
