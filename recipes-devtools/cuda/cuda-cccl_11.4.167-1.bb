CUDA_PKG = "cuda-cccl"

require cuda-shared-binaries.inc

MAINSUM = "3b2e4c615299ab19f57d450f32f2af38d02849e8aa578b100b0b21cfe38a8510"
MAINSUM:x86-64 = "72a0d85dd048caef58ee12f6b2aef086335df4262ac1567e437e480e49007412"

CONTAINER_CSV_FILES = ""
CONTAINER_CSV_PKGNAME = ""
FILES:${PN} = "${prefix}/local/cuda-${CUDA_VERSION}/include"
FILES:${PN}-dev = ""

BBCLASSEXTEND = "native nativesdk"
