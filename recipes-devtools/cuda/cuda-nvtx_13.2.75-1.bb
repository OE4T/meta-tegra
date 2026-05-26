CUDA_PKG = "${BPN}"
CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

MAINSUM = "e45a99317ddb38d0962b53b0c1e1b9d68f66cd86c647b858edca459303527a0c"
MAINSUM:x86-64 = "c6ff872c4f57b11dbdb12fa983f80656e4c5af8e5fcdf65aa799a7c800da68dc"

BBCLASSEXTEND = "native nativesdk"
