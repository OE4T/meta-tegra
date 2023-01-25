CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "0339ff5ad99aa44aa7a0c07af21b679f6ff4ea5577cfa1e3fa5352e7f7977cf0"
MAINSUM:x86-64 = "906a7e7a0d24ae7ab56fc2f9ea88aa9de964301116fbd547f0875d4044a25e83"

BBCLASSEXTEND = "native nativesdk"
