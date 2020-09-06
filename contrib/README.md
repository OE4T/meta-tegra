Contributed recipes for meta-tegra
==================================

This layer contains recipes to support builds with
meta-tegra that are not directly part of the BSP.

## gcc 8 toolchain
The main use of this layer is to provide an older
gcc toolchain that is compatible with the version
of the CUDA toolkit for the Jetson platforms.

For more information, see
[this wiki page](https://github.com/OE4T/meta-tegra/wiki/Using-gcc8-from-the-contrib-layer).

## gstreamer 1.14 recipes
This layer also includes a port of the gstreamer 1.14
recipes from the OE-Core warrior branch. When using the
NVIDIA container runtime for Jetson platforms, you may
need to use gstreamer 1.14 to ensure that the gstreamer
libraries and plugins exported into NVIDIA's containers
(which do not bundle them, but mount them from the underlying
OS) are compatible.

To use these recipes, add this line to your build
configuration:

    require conf/include/gstreamer-1.14.conf

after including this layer in your `bblayers.conf` file.


Contributing
------------

Please use GitHub (https://github.com/OE4T/meta-tegra) to submit
issues or pull requests, or add to the documentation on the wiki.
Contributions are welcome!
