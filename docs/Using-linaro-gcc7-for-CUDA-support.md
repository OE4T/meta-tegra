# Overview
As mentioned in the README, OE-Core removed gcc7 from support starting with the warrior release.  However, CUDA 10 does not support gcc8.  This means you need to pull in another layer or changes which support gcc7 toolchain in order to support CUDA 10.0.

Fortunately adding gcc7 does not require a lot of work to achieve if using the meta-linaro project.  See tested instructions below.

## Instructions for warrior branch
1. Add the [meta-linaro-toolchain](https://git.linaro.org/openembedded/meta-linaro.git/) layer as a submodule in your project by cloning this project, checking out the appropriate branch (warrior).
2. Use ```bitbake-layers add-layer``` to add the meta-linaro/meta-linaro-toolchain layer to your project in ```build/conf/bblayers.conf```.  You can add just the meta-linaro-toolchain folder and not the entire meta-linaro layer.
3. Reference the GCC version in your ```build/conf/local.conf``` like this:
```
GCCVERSION = "linaro-7.%"
```
4. Add these lines to your ```build/conf/local.conf``` to prevent errors like "cannot compute suffix of object files" due to missing fmacro-prefix-map support on GCC7 and based on the [default setting on the warrior branch](https://git.yoctoproject.org/cgit/cgit.cgi/poky/tree/meta/conf/bitbake.conf?h=warrior#n613):
```
# GCC 7 doesn't support fmacro-prefix-map, results in "error: cannot compute suffix of object files: cannot compile"
# Change the value from bitbake.conf DEBUG_PREFIX_MAP to remove -fmacro-prefix-map
DEBUG_PREFIX_MAP = "-fdebug-prefix-map=${WORKDIR}=/usr/src/debug/${PN}/${EXTENDPE}${PV}-${PR} \
                    -fdebug-prefix-map=${STAGING_DIR_HOST}= \
                    -fdebug-prefix-map=${STAGING_DIR_NATIVE}= \
                    "
```
5. For recipes which fail during the configuration stage with messages like this:
```
cc1: error: -Werror=missing-attributes: no option -Wmissing-attributes
cc1: error: -Werror=missing-attributes: no option -Wmissing-attributes
```
Add a .bbappend to your layer which removes the unsupported missing-attributes flag from respective CPPFLAGS for host and target compile.  For instance, to resolve with libxcrypt you can add a ```/recipes-core/libxcrypt/libxcrypt.bbappend``` to your layer with content:
```
# For GCC7 support
TARGET_CPPFLAGS = "-I${STAGING_DIR_TARGET}${includedir}"
CPPFLAGS_append_class-nativesdk = ""
```
Note that the `libxcrypt` recipe in OE-Core's `warrior` branch was updated in September 2019 (for Yocto Project 2.7.2) to remove the compiler option that causes this error with older compilers.