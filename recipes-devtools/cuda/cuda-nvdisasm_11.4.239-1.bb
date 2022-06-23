CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

COMPATIBLE_HOST:x86-64 = "(-)"
MAINSUM = "5c74f610b67fb91b75d80ca04d1a8f08d41806da46c3014f5a97c534f37805d1"

BBCLASSEXTEND = "native nativesdk"
