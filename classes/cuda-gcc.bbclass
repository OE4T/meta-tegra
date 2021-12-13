# CUDA requires gcc/g++ 8, so add that compiler and its runtime as
# dependencies, and set CC_FOR_CUDA and CXX_FOR_CUDA to point to that compiler.
DEPENDS:append:cuda = " virtual/${TARGET_PREFIX}cuda-gcc gcc-8-runtime"
CUDA_HOST_TOOLCHAIN_SUFFIX ??= ""
CUDA_HOST_TOOLCHAIN_SUFFIX:cuda = "-8.5.0"
CC_FOR_CUDA ?= "${CCACHE}${HOST_PREFIX}gcc${CUDA_HOST_TOOLCHAIN_SUFFIX} ${HOST_CC_ARCH}${TOOLCHAIN_OPTIONS}"
CXX_FOR_CUDA ?= "${CCACHE}${HOST_PREFIX}g++${CUDA_HOST_TOOLCHAIN_SUFFIX} ${HOST_CC_ARCH}${TOOLCHAIN_OPTIONS}"
PACKAGE_ARCH:cuda = "${TEGRA_PKGARCH}"
