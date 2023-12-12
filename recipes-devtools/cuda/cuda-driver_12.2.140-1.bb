CUDA_PKG = "${BPN}-dev"

require cuda-shared-binaries.inc

DEPENDS:tegra = "tegra-libraries-cuda"

L4T_DEB_GROUP = "cuda-cudart"
DEVSUM = "9ffbbfe9200cd08fe19cf21f390083a932ea0432b5d3c0abf9191d363e6126f2"
DEVSUM:x86-64 = "0523e4da56f4312d1098dfb71aab94f223aac0af8c73f89311f49b3c5c2bc87f"

ALLOW_EMPTY:${PN} = "1"
EXCLUDE_PACKAGES_FROM_SHLIBS = ""
PRIVATE_LIBS = "libcuda.so.1"

BBCLASSEXTEND = "native nativesdk"
