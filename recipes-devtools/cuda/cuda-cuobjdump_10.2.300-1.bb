CUDA_PKG = "${BPN}"

require cuda-shared-binaries-${PV}.inc

MAINSUM = "1a6c02ae2786fadeac0888e8998d47e28282319b36909b6cdc3c0a546afa578a"
MAINSUM:x86-64 = "0fa1d9efdb12fdac7fca515798007e8014ba4953d54fe431b0756db41edf6938"
BBCLASSEXTEND = "native nativesdk"
