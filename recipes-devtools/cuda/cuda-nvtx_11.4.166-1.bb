CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "737d3e1f6b8d2c82aa16c737f69f4a850d0ba53584726c175e85206092ae91a2"
MAINSUM:x86-64 = "ab5232243dc8e3b3cad1115124b5ce58bab0d9936d09b3e20a51e1f2726e46a0"

BBCLASSEXTEND = "native nativesdk"
