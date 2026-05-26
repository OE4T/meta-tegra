CUDA_PKG = "${BPN}"
CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

MAINSUM = "ff19dfb7d419eefbd1fbf6fba32e95189a24603119cf50e099f4df3da48ea37b"
MAINSUM:x86-64 = "4ae6efd749f80d0d513c27490874e43e1e5bf9b33c60f9b51014f9d70b9d7a50"

ALLOW_EMPTY:${PN} = "1"
BBCLASSEXTEND = "native nativesdk"
