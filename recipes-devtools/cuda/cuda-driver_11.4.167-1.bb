CUDA_PKG = "${BPN}-dev"

require cuda-shared-binaries.inc

DEPENDS:tegra = "tegra-libraries-cuda"

L4T_DEB_GROUP = "cuda-cudart"
DEVSUM = "4b0f3a1c27fe05ed2615438bc045a1cae60430b2f41780c729ebb6bab4853e7c"
DEVSUM:x86-64 = "160bb6077f8d2991fc665f368a570c451b46cb47a81d1691b5d9dbd217d9e2c7"

ALLOW_EMPTY:${PN} = "1"
EXCLUDE_PACKAGES_FROM_SHLIBS = ""
PRIVATE_LIBS = "libcuda.so.1"

BBCLASSEXTEND = "native nativesdk"
