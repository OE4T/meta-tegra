CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "55ddbcf3392287dbb3d68476ad19c620d0aa18b67af00cc74a03601545341425"
MAINSUM:x86-64 = "a222691cbe03cc8a4f8a7de048ec8346ae6ba79addb3310f3bf577677293aefe"

BBCLASSEXTEND = "native nativesdk"
