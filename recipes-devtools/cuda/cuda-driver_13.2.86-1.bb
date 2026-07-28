CUDA_PKG = "${BPN}-dev"

require cuda-shared-binaries.inc

DEPENDS:tegra = "tegra-libraries-cuda tegra-cuda-utils"

L4T_DEB_GROUP = "cuda-cudart"
DEVSUM = "e84fa1570d5126311465037f4d5f9e57a4397f18f0610f0d0f8c9d99b213db73"
DEVSUM:x86-64 = "0f3d44946539c53946bfe3e9642ecb4a39149e5979bf534ed45c1cbef83520e4"

ALLOW_EMPTY:${PN} = "1"
EXCLUDE_PACKAGES_FROM_SHLIBS = ""
PRIVATE_LIBS = "libcuda.so.1"

BBCLASSEXTEND = "native nativesdk"
