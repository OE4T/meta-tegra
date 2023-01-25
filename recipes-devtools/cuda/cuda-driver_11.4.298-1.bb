CUDA_PKG = "${BPN}-dev"

require cuda-shared-binaries.inc

DEPENDS:tegra = "tegra-libraries-cuda"

L4T_DEB_GROUP = "cuda-cudart"
DEVSUM = "8366c37ee1088cc27cb48b572de612bb2f1df5a4598c7b59a6a0aeebc0c8bede"
DEVSUM:x86-64 = "0fb16be874f3061d7cad5580e85e1e6dc5f8cdd355b534c6dcbe80a46956e07e"

ALLOW_EMPTY:${PN} = "1"
EXCLUDE_PACKAGES_FROM_SHLIBS = ""
PRIVATE_LIBS = "libcuda.so.1"

BBCLASSEXTEND = "native nativesdk"
