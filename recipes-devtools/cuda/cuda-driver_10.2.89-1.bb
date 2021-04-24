REPLACE_STUBS = "0"
CUDA_PKG = "${BPN}-dev"

require cuda-shared-binaries-${PV}.inc

DEVSUM = "c1c55ba59d8a28a7d56800504c65683a4d392893f35d08ec7bddf6b45efda468"
DEVSUM_x86-64 = "4e57a63577872f1b8ae9a65b81b4d015f3c9c7533e81f48655d33ecca611caf3"

ALLOW_EMPTY_${PN} = "1"
PRIVATE_LIBS = "libcuda.so.1"

BBCLASSEXTEND = "native nativesdk"
