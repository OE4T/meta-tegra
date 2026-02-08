**Update 16-Dec-2021:** The master branch has support for restricting the use of the
older gcc toolchain *just* for CUDA compilations, and the meta-tegra main layer includes
the recipes to support this. You no longer need to use an older toolchain for building
everything, and the recipes for the older toolchains have been dropped from
the contrib layer.  See [#867](https://github.com/OE4T/meta-tegra/pull/867) for more information.

# For honister and earlier branches

With the JetPack 4.4 Developer Preview release (L4T R32.4.2), NVIDIA updated CUDA support
for the Jetson platforms to CUDA 10.2, which is compatible with GCC 8. On the `dunfell-l4t-r32.4.2`
and `master` branches, the `contrib` layer in this repository has been updated to include recipes
for the gcc 8 toolchain, imported from the OE-Core `warrior` branch. If you intend to build
packages that use CUDA, you should configure your build to use GCC 8.

If you have previously configured your builds for GCC 7 when using an earlier version of
meta-tegra with an older L4T/JetPack release, you can retain those settings and continue to
use GCC, as builds should be compatible with either version of the toolchain.

## Configuring your builds for GCC 8

Follow the steps below to switch to GCC 8:

1. Use `bitbake-layers add-layer` to add the `meta-tegra/contrib` layer to your project in ```build/conf/bblayers.conf```.
2. Select GCC version in your `build/conf/local.conf` and use the required configuration like this:
```
GCCVERSION = "8.%"
```
or
```
GCCVERSION_aarch64 = "8.%"
```
if you have other platforms (with other CPU architectures) in your build setup that require
the latest toolchain provided by OE-Core.