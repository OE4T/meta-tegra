Contributed recipes for meta-tegra
==================================

This layer contains recipes to support builds with
meta-tegra that are not directly part of the BSP.

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

Getting Help, Reporting Issues, Contributing
--------------------------------------------

Please see the [README file in the meta-tegra layer](../README.md)
for more information.
