CUDA_PKG = "${BPN}"
L4T_DEB_GROUP = "cuda-nvcc"

require cuda-shared-binaries.inc

MAINSUM = "b95d26f0e63f113e6e2f5b8578841644de549293679d65c70e112164d2080311"
MAINSUM:x86-64 = "b539ec5aff868631d55fd296c30d4e8046df69ec61fde801e54215b2b0b2a9cd"

ALLOW_EMPTY:${PN} = "1"
BBCLASSEXTEND = "native nativesdk"
