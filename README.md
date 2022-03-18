OpenEmbedded/Yocto BSP layer for NVIDIA Jetson TX1/TX2/AGX Xavier/Nano
======================================================================

Linux4Tegra release: R32.7.1
JetPack release:     4.6.1

Boards supported:
* Jetson-TX1 development kit
* Jetson-TX2 development kit
* Jetson TX2-NX module in Xavier NX dev kit carrier
* Jetson AGX Xavier development kit
* Jetson Xavier NX Development Kit
* Jetson Xavier NX eMMC module in dev kit or Nano carrier board
* Jetson Nano development kit
* Jetson Nano eMMC module with rev B01 carrier board
* Jetson Nano 2GB development kit

Also supported thanks to community support:
* Jetson-TX2i module
* Jetson-TX2 4GB module

Not yet supported:
* Jetson AGX Xavier Industrial module


This layer depends on:
URI: git://git.openembedded.org/openembedded-core
branch: dunfell
LAYERSERIES_COMPAT: dunfell


PLEASE NOTE
-----------

* CUDA 10.2 supports up through gcc 8 only. Pre-built binaries
  in the BSP appear to be compatible with gcc 7 and 8 **only**.
  So use only gcc 7 or gcc 8 if you intend to use CUDA.
  Recipes for gcc 8 have been imported from the OE-Core warrior branch
  (the last version of OE-Core to supply gcc 8) to make it easier
  to use this older toolchain.

  See [this wiki page](https://github.com/OE4T/meta-tegra/wiki/Using-gcc8-from-the-contrib-layer)
  for information on adding the `meta-tegra/contrib` layer to your
  builds and configuring them for GCC 8.


Contributing
------------

Please see the contributor wiki page at
[this link](https://github.com/OE4T/meta-tegra/wiki/OE4T-Contributor-Guide).

Contributions are welcome!
