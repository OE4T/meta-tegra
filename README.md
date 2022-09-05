OpenEmbedded/Yocto BSP layer for NVIDIA Jetson Modules
======================================================

Linux4Tegra release: R35.1.0
JetPack release:     5.0.2

Boards supported:
* Jetson AGX Xavier development kit
* Jetson Xavier NX Development Kit
* Jetson Xavier NX eMMC module in dev kit or Nano carrier board
* Jetson AGX Orin development kit

Community supported:
* Clara AGX development kit

This layer depends on:
URI: git://git.openembedded.org/openembedded-core
branch: master
LAYERSERIES_COMPAT: langdale


CUDA toolchain compatibility note
---------------------------------

CUDA 11.4 supports up through gcc 10 only, so recipes are included
for adding the gcc 8 toolchain to the build for CUDA use, and `cuda.bbclass`
has been updated to pass the g++ 8 compiler to nvcc for CUDA code compilation.


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
