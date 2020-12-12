OpenEmbedded/Yocto BSP layer for NVIDIA Jetson TX1/TX2/AGX Xavier/Nano
======================================================================

Linux4Tegra release: R32.4.4
JetPack release:     4.4.1

Boards supported:
* Jetson-TX1 development kit
* Jetson-TX2 development kit
* Jetson AGX Xavier development kit
* Jetson Nano development kit
* Jetson Nano eMMC module with rev B01 carrier board
* Jetson Nanon 2GB development kit
* Jetson Xavier NX Development Kit
* Jetson Xavier NX eMMC module in dev kit or Nano carrier board

Also supported thanks to community support:
* Jetson-TX2i module
* Jetson-TX2 4GB module
* Jetson AGX Xavier 8GB module


This layer depends on:
URI: git://git.openembedded.org/openembedded-core
branch: master
LAYERSERIES_COMPAT: gatesgarth


PLEASE NOTE
-----------

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

      CUDA_BINARIES = "cuda-binaries-ubuntu1604"

  to your build configuration so the CUDA recipes can find
  them. Otherwise, the recipes will default to looking for
  the Ubuntu 18.04 package.

* CUDA 10.2 supports up through gcc 8 only. Pre-built binaries
  in the BSP appear to be compatible with gcc 7 and 8 **only**.
  So use only gcc 7 or gcc 8 if you intend to use CUDA.
  Recipes for gcc 8 have been imported from the OE-Core warrior branch
  (the last version of OE-Core to supply gcc 8) to make it easier
  to use this older toolchain.

  See [this wiki page](https://github.com/OE4T/meta-tegra/wiki/Using-gcc8-from-the-contrib-layer)
  for information on adding the `meta-tegra/contrib` layer to your
  builds and configuring them for GCC 8.


Getting Help
-----------

For general build issues or questions about getting started with your build
setup please use the
[Discussions](https://github.com/OE4T/meta-tegra/discussions) tab of the
meta-tegra repository:

* Use the Ideas category for anything you'd like to see included in meta-tegra,
Wiki content, or the
[tegra-demo-distro](https://github.com/OE4T/tegra-demo-distro/issues).
* Use the Q&A category for questions about how to build or modify your Tegra
target based on the content here.
* Use the "Show and Tell" category for any projects you'd like to share which
are related to meta-tegra.
* Use the General channel for anything that doesn't fit well into the categories
above, and which doesn't relate to a build or runtime issue with Tegra yocto
builds.


Reporting Issues
-----------

Use the [Issues tab in meta-tegra](https://github.com/OE4T/meta-tegra/issues)
for reporting build or runtime issues with Tegra yocto build targets.  When
reporting build or runtime issues, please include as much information about your
environment as you can. For example, the target hardware you are building for,
branch/version information, etc.  Please fill in the provided bug template when
reporting issues.


Contributing
------------

Please see [CONTRIBUTING.md](CONTRIBUTING.md)

Contributions are welcome!
