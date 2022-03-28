CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "2cb614a67522c0e66328a5f987950cad9daedd2ff8a4a0161ecde56890d5c3f6"
MAINSUM:x86-64 = "e46ffe3c556bf747c1e4e3a2f50d961e0a2e24c6692f8c2c3d0338cbb702e790"

BBCLASSEXTEND = "native nativesdk"
