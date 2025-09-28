CUDA_PKG = "${BPN}"
CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

MAINSUM = "bc652397c7e20680d74fed7d13be7e21b2cf4bc9bf936aa035607cf6e1d381d9"
MAINSUM:x86-64 = "ebb9c0940b103cb7ce6510d698ee5c0d423b3196137f60aab2a09c046a6a1caa"

BBCLASSEXTEND = "native nativesdk"
