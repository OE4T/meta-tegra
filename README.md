OpenEmbedded/Yocto BSP layer for NVIDIA Jetson Modules
======================================================

Jetson Linux release: R35.6.2
JetPack release:      5.1.5

Boards supported:
* Jetson AGX Xavier development kit
* Jetson Xavier NX Development Kit
* Jetson Xavier NX eMMC module in dev kit or Nano carrier board
* Jetson AGX Orin development kit
* Jetson Orin NX 16GB (p3767-0000) in Xavier NX (p3509) carrier
* Jetson Orin NX 16GB (p3767-0000) in Orin Nano (p3768) carrier
* Jetson Orin Nano development kit
* Jetson AGX Orin Industrial 64GB (P3701-0008) in Orin AGX (P3737) carrier

Community supported:
* Clara AGX development kit
* Jetson AGX Xavier Industrial

This layer depends on:
URI: git://git.openembedded.org/openembedded-core
branch: kirkstone
LAYERSERIES_COMPAT: kirkstone


CUDA toolchain compatibility note
---------------------------------

CUDA 11.4 supports up through gcc 10 only, so recipes are included
for adding the gcc 10 toolchain to the build for CUDA use, and `cuda.bbclass`
has been updated to pass the g++ 10 compiler to nvcc for CUDA code compilation.


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
