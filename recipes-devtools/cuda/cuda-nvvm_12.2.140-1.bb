CUDA_PKG = "${BPN}"
L4T_DEB_GROUP = "cuda-nvcc"

require cuda-shared-binaries.inc

MAINSUM = "c6931d80a2c823e4c0a42dd1371e7bda613be2444393a67970e32f05dd0ccfd2"
MAINSUM:x86-64 = "7cd46649dbad60b59860f0965d92eea87f783abfe2cbd2c9b49a0c8544161e3a"

BBCLASSEXTEND = "native nativesdk"
