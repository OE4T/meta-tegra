CUDA_PKG = "cuda-cccl"
L4T_DEB_GROUP = "cuda-thrust"

require cuda-shared-binaries.inc

COMPATIBLE_HOST:x86-64 = "(-)"
MAINSUM = "7d1cd8f4526ecc031416a23293859bc3095170fe8c7f3c68d11cdc9ef4f90ddd"

CONTAINER_CSV_FILES = ""
CONTAINER_CSV_PKGNAME = ""
FILES:${PN} = "${prefix}/local/cuda-${CUDA_VERSION}/include"
FILES:${PN}-dev = ""

BBCLASSEXTEND = "native nativesdk"
