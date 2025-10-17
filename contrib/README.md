Contributed recipes for meta-tegra
==================================

This layer contains recipes to support builds with
meta-tegra that are not directly part of the BSP.

## gstreamer 1.14 recipes
This layer also includes a port of the gstreamer 1.14
recipes from the OE-Core warrior branch. These recipes
were sometimes required for running containers from the
NVIDIA NGC catalog, prior to the introduction of the
tegra-container-passthrough recipe. The recipes and
configuration file for using them have been retained
for backward compatibility, but should no longer be
needed or used.

Getting Help, Reporting Issues, Contributing
--------------------------------------------

Please see the [README file in the meta-tegra layer](../README.md)
for more information.
