CUDA_PKG = "${BPN}"

require cuda-shared-binaries-${PV}.inc

MAINSUM = "c77f9f554932d7586d58c5cc77b62ae94f2c1a9779aac78fd8b89335b6ad35ba"
MAINSUM:x86-64 = "1c68ce0ec640dba780c5e0fc10f740d36c45cc29a5609c63ee64c826a38854df"

BBCLASSEXTEND = "native nativesdk"
