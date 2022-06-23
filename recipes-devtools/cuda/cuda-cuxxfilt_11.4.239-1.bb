CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

COMPATIBLE_HOST:x86-64 = "(-)"
MAINSUM = "55ddbcf3392287dbb3d68476ad19c620d0aa18b67af00cc74a03601545341425"

BBCLASSEXTEND = "native nativesdk"
