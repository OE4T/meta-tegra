OpenEmbedded/Yocto BSP layer for NVIDIA Jetson TX1/TX2/AGX Xavier/Nano
======================================================================

Jetson Linux release: R32.7.6
JetPack release:      4.6.6

Boards supported:
* Jetson-TX1 development kit
* Jetson-TX2 development kit
* Jetson TX2-NX module in Xavier NX dev kit carrier
* Jetson AGX Xavier development kit
* Jetson Nano development kit
* Jetson Nano eMMC module with rev B01 carrier board
* Jetson Nano 2GB development kit
* Jetson Xavier NX Development Kit
* Jetson Xavier NX eMMC module in dev kit or Nano carrier board

Also supported thanks to community support:
* Jetson-TX2i module
* Jetson-TX2 4GB module


This layer depends on:
URI: git://git.openembedded.org/openembedded-core
branch: kirkstone
LAYERSERIES_COMPAT: kirkstone

Final JetPack 4 Release
-----------------------

This is the last release for JetPack 4 and the Jetson Linux R32 series, which is now at end of life.
See [this NVIDIA developer forum post](https://forums.developer.nvidia.com/t/announcing-end-of-life-for-nvidia-jetpack-4-with-the-release-of-jetpack-4-6-6/314409) for details.


CUDA toolchain compatibility note
---------------------------------

CUDA 10.2 supports up through gcc 8 only, so recipes are included
for adding the gcc 8 toolchain to the build for CUDA use, and `cuda.bbclass`
has been updated to pass the g++ 8 compiler to nvcc for CUDA code compilation.
This is different from earlier releases/branches, which required setting
the toolchain used for all builds to a CUDA-compatible version.


Getting Help
------------

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
----------------

Use the [Issues tab in meta-tegra](https://github.com/OE4T/meta-tegra/issues)
for reporting build or runtime issues with Tegra yocto build targets.  When
reporting build or runtime issues, please include as much information about your
environment as you can. For example, the target hardware you are building for,
branch/version information, etc.  Please fill in the provided bug template when
reporting issues.

We are required to provide an e-mail address, but please use GitHub as
described above, instead of sending e-mail to oe4t-questions@madison.systems.

Contributing
------------

Please see [CONTRIBUTING.md](CONTRIBUTING.md) for information on submitting
patches to the maintainers.

Contributions are welcome!
