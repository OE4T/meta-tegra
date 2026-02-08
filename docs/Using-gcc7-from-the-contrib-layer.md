Starting with the `warrior` branch, meta-tegra includes a `contrib` layer with user-contributed recipes
for optional inclusion in your builds.  The layer includes recipes for gcc7 that you can use for compatibility
with CUDA 10.0.

## Configuring your builds for GCC 7

Follow the steps below to switch to GCC 7:

1. Use `bitbake-layers add-layer` to add the `meta-tegra/contrib` layer to your project in ```build/conf/bblayers.conf```.
2. Select GCC version in your `build/conf/local.conf` and use the required configuration like this:
```
GCCVERSION = "7.%"
require contrib/conf/include/gcc-compat.conf
```

## Troubleshooting

Older GCC versions, such as GCC 7, does NOT support `fmacro-prefix-map`. As a result, [due to the default settings](https://git.yoctoproject.org/cgit/cgit.cgi/poky/tree/meta/conf/bitbake.conf?h=warrior#n613), while building newer releases of the Yocto Project, for example Warrior, with older GCC version you may get errors like "cannot compute suffix of object files". To fix add the following lines to your `build/conf/local.conf`:
```
# GCC 7 doesn't support fmacro-prefix-map, results in "error: cannot compute suffix of object files: cannot compile"
DEBUG_PREFIX_MAP_remove = "-fmacro-prefix-map=${WORKDIR}=/usr/src/debug/${PN}/${EXTENDPE}${PV}-${PR}"
```

**NOTE:** This configuration is applied in `contrib/conf/include/gcc-compat.conf`. No further actions are required if you have already required it in `build/conf/local.conf`.

## See Also

* [Working with NVIDIA Tegra BSP and Supporting Latest CUDA Versions, Leon Anavi, Yocto Dev Summit 2019](https://www.yoctoproject.org/working-with-nvidia-tegra-bsp-and-supporting-latest-cuda-versions-yocto-project-summit-2019/) [slides](https://wiki.yoctoproject.org/wiki/images/4/43/Yocto-dev-summit-leon-anavi-2019.pdf)
