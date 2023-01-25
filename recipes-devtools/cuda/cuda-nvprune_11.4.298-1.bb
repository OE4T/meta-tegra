CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "5d532833c9186be93e9411121eb73a32269de09c989d3c164f0aa27a49cef125"
MAINSUM:x86-64 = "eb7f79be09f9e9825aecc2cd7a1ba3dbf009bec26e72eb2b522c72939f2b1f4c"

BBCLASSEXTEND = "native nativesdk"
