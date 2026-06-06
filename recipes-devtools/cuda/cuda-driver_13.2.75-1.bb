CUDA_PKG = "${BPN}-dev"
CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

DEPENDS:tegra = "tegra-libraries-cuda tegra-cuda-utils"

L4T_DEB_GROUP = "cuda-cudart"
DEVSUM = "58ae46716b0b43ff7b4a7e43211832cc62258f8b266a4f65a6bc6986d4a8ec4a"
DEVSUM:x86-64 = "32a1939bb2da9c0db87a5e0fc40f6fca559c37590cff50151807bffcf83d4da0"

ALLOW_EMPTY:${PN} = "1"
EXCLUDE_PACKAGES_FROM_SHLIBS = ""
PRIVATE_LIBS = "libcuda.so.1"

BBCLASSEXTEND = "native nativesdk"
