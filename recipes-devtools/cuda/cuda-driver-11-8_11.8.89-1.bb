CUDA_PKG="cuda-driver-dev"

require cuda-shared-binaries-11.8.inc

DEPENDS:tegra = "tegra-libraries-cuda"

L4T_DEB_GROUP = "cuda-cudart"
DEVSUM = "d4a5aa51f7d8e4d6466af8dcd853750f1c2f3b19756214a8e8acd362cc53a1fe"
DEVSUM:x86-64 = "ee491628df1b08a633d7ca580d11d6aad48bd95af8e5ddb5c382d19ce1fc3596"

ALLOW_EMPTY:${PN} = "1"
EXCLUDE_PACKAGES_FROM_SHLIBS = ""
PRIVATE_LIBS = "libcuda.so.1"

BBCLASSEXTEND = "native nativesdk"
