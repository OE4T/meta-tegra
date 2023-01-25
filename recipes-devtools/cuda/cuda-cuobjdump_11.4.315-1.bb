CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "74acf4489ec6384e81c7afef8be65c72244631e8e64c30a5a3971e46b7d37559"
MAINSUM:x86-64 = "8d1b5e1ee185db587ee32748041b7dc9375a2c6ba314b36bcf797e58a82282d8"
BBCLASSEXTEND = "native nativesdk"
