CUDA_PKG = "${BPN}"
CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

MAINSUM = "ae228663c817bd92a5fcc2bcb1c0854741ec1a6582bfd597fc4e202e77bfc3cc"
MAINSUM:x86-64 = "6ac1362b970889cb6c9071c55af49371c8ab5c1f77e05e3347c4af7832607c81"

ALLOW_EMPTY:${PN} = "1"
BBCLASSEXTEND = "native nativesdk"
