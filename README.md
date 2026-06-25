OpenEmbedded/Yocto BSP layer for NVIDIA Jetson Modules
======================================================

Jetson Linux release: R39.2.0
JetPack release:      7.2

Boards supported:
* Jetson AGX Thor development kit
* Jetson AGX Orin development kit
* Jetson Orin NX 16GB (p3767-0000) in Xavier NX (p3509) carrier
* Jetson Orin NX 16GB (p3767-0000) in Orin Nano (p3768) carrier
* Jetson Orin Nano development kit
* Jetson AGX Orin Industrial 64GB (P3701-0008) in Orin AGX (P3737) carrier


This layer depends on:
URI: https://git.openembedded.org/openembedded-core
branch: master
LAYERSERIES_COMPAT: blacksail

See [this page](docs/release-notes/JetPack-7.2-L4T-R39.2.0-Notes.md) for information
about changes in this release.

Getting Help
------------

Our documentation is available [here](https://oe4t.github.io). You can
also find documentation in Markdown form in the [docs](docs) directory.

For general build issues or questions about getting started with your build
setup please use the
[Discussions](https://github.com/OE4T/meta-tegra/discussions) tab of the
meta-tegra repository:

* Use the Ideas category for anything you'd like to see included in the layer
  or the wiki content.
* Use the Q&A category for questions about the layer, recipes, etc.
* Use the "Show and Tell" category for any projects you'd like to share which
are related to the layer.
* Use the General channel for anything that doesn't fit well into the categories
above, and which doesn't relate to a build or runtime issue with Yocto/OE
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
