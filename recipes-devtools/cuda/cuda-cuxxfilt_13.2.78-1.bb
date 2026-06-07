CUDA_PKG = "${BPN}"
CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

MAINSUM = "ac9bd1be3cc437838f18747159be70b3ba85b69b6b93577ca3fe03e9e6132ad2"
MAINSUM:x86-64 = "d044719f718ad7fc604d7c80f4b8ed4c9c546c8896a0cb48945471f5d44cce5a"

BBCLASSEXTEND = "native nativesdk"
