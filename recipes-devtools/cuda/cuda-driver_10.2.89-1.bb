REPLACE_STUBS = "0"
CUDA_PKG = "${BPN}-dev"

require cuda-shared-binaries-${PV}.inc

SRC_URI[dev.sha256sum] = "c1c55ba59d8a28a7d56800504c65683a4d392893f35d08ec7bddf6b45efda468"

ALLOW_EMPTY_${PN} = "1"
PRIVATE_LIBS = "libcuda.so.1"

BBCLASSEXTEND = "native nativesdk"
