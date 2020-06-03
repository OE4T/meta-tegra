OpenEmbedded/Yocto BSP layer for NVIDIA Jetson TX1/TX2/AGX Xavier/Nano
======================================================================

Linux4Tegra release: R32.4.2
JetPack release:     4.4 Developer Preview

Boards supported:
* Jetson-TX1 development kit
* Jetson-TX2 development kit
* Jetson AGX Xavier development kit
* Jetson Nano development kit
* Jetson Nano eMMC module with rev B01 carrier board

Experimental support:
* Jetson Xavier NX Development Kit
* Jetson Xavier NX eMMC module in dev kit or Nano carrier board

Also supported thanks to community support:
* Jetson-TX2i module
* Jetson-TX2 4GB module
* Jetson AGX Xavier 8GB module


This layer depends on:
URI: git://git.openembedded.org/openembedded-core
branch: dunfell
LAYERSERIES_COMPAT: dunfell


PLEASE NOTE
-----------

* NVIDIA recommends using L4T R32.3.1/JetPack 4.3 for
  production use. The JetPack release supported here
  is labeled a "developer preview".

* Some packages outside the L4T BSP can only be downloaded
  with an NVIDIA Developer Network login - in particular,
  the CUDA host-side tools.

  To use any packages that require a Devnet login, you must
  create a Devnet account and download the JetPack packages
  you need for your builds using NVIDIA SDK Manager.

  You must then set the variable NVIDIA_DEVNET_MIRROR to
  "file://path/to/the/downloads" in your build configuration
  (e.g., local.conf) to make them available to your bitbake
  builds.  This can be the NVIDIA SDK Manager downloads
  directory, `/home/$USER/Downloads/nvidia/sdkm_downloads`

* The SDK Manager downloads a different package of CUDA host-side
  tools depending on whether you are running Ubuntu 16.04
  or 18.04. If you downloaded the Ubuntu 16.04 package, you
  should add

      CUDA_BINARIES_NATIVE = "cuda-binaries-ubuntu1604-native"

  to your build configuration so the CUDA recipes can find
  them. Otherwise, the recipes will default to looking for
  the Ubuntu 18.04 package.

* CUDA 10.2 supports up through gcc 8 only. Pre-built binaries
  in the BSP appear to be compatible with gcc 7 and 8 **only**.
  So use only gcc 7 or gcc 8 if you intend to use CUDA.
  Recipes for gcc 8 have been imported from the OE-Core warrior branch
  (the last version of OE-Core to supply gcc 8) to make it easier
  to use this older toolchain.

  See [this wiki page](https://github.com/madisongh/meta-tegra/wiki/Using-gcc8-from-the-contrib-layer)
  for information on adding the `meta-tegra/contrib` layer to your
  builds and configuring them for GCC 8.


Contributing
------------

Please use GitHub (https://github.com/madisongh/meta-tegra) to submit
issues or pull requests, or add to the documentation on the wiki.
Contributions are welcome!
