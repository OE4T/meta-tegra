CUDA_PKG = "${BPN}"

require cuda-shared-binaries-${PV}.inc

MAINSUM = "2027d40d3302ecfbfa42790b365f8c097726d5a8b05fb7087971dec7e6bc1f35"
MAINSUM:x86-64 = "cf0046eb26ff279a78c2ce4a2661ba2f5a870609423beb73c6c4e2350fdc4739"

BBCLASSEXTEND = "native nativesdk"
