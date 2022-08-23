CUDA_PKG = "cuda-cccl"
L4T_DEB_GROUP = "cuda-thrust"

require cuda-shared-binaries.inc

MAINSUM = "7d1cd8f4526ecc031416a23293859bc3095170fe8c7f3c68d11cdc9ef4f90ddd"
MAINSUM:x86-64 = "a649073b4ecbcaeffc1da5ed8c86e27afe642dd6fbc04e2bea3652be2b3c346b"

FILES:${PN} = "${prefix}/local/cuda-${CUDA_VERSION}/include"
FILES:${PN}-dev = ""

BBCLASSEXTEND = "native nativesdk"
