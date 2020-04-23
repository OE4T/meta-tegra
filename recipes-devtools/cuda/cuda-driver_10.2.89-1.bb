REPLACE_STUBS = "0"

require cuda-shared-binaries-${PV}.inc

ALLOW_EMPTY_${PN} = "1"
PRIVATE_LIBS = "libcuda.so.1"

BBCLASSEXTEND = "native nativesdk"
