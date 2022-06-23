CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

COMPATIBLE_HOST:x86-64 = "(-)"
MAINSUM = "441041da2f84f0c34bed5222dafc7150daf50e363fddcf5e36166f3c5320bbde"
BBCLASSEXTEND = "native nativesdk"
