CUDA_PKG = "${BPN}-dev"

require cuda-shared-binaries.inc

DEPENDS:tegra = "tegra-libraries-cuda tegra-cuda-utils"

L4T_DEB_GROUP = "cuda-cudart"
DEVSUM = "0c453507da39a28ca214723c8980c5a4832d8ea3e8f28dfd8ac83a1b2055dda1"
DEVSUM:x86-64 = "296c391b1d7fba881180595693a7e2742c3529c30f4909cd0b00a0cae8d2294e"

ALLOW_EMPTY:${PN} = "1"
EXCLUDE_PACKAGES_FROM_SHLIBS = ""
PRIVATE_LIBS = "libcuda.so.1"

BBCLASSEXTEND = "native nativesdk"
