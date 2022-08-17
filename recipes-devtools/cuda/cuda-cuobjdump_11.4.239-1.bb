CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "441041da2f84f0c34bed5222dafc7150daf50e363fddcf5e36166f3c5320bbde"
MAINSUM:x86-64 = "059b27e0ee74a9e64b051ac0fb7c00c64719970794621d3480fd128991b1502a"
BBCLASSEXTEND = "native nativesdk"
