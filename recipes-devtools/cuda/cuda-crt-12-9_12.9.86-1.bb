CUDA_PKG = "cuda-crt"
L4T_DEB_GROUP = "cuda-nvcc"

require cuda-shared-binaries-12.9.inc

MAINSUM = "3580531b3cf768220942f2f5c21b99e82889479ad1b1ea96b2e4eecf46c984b1"
MAINSUM:x86-64 = "364c82d5fd53b6687aca004c4583fd119a98e0fe72798d88b0fdedc56ad5aecf"

ALLOW_EMPTY:${PN} = "1"
BBCLASSEXTEND = "native nativesdk"
