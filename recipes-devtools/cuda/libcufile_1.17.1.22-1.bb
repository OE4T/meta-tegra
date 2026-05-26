CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

MAINSUM = "6ac612569895fc0ec6f07c6a9d1b382b87629418495aa0d4454c7b2fe7d30ee3"
MAINSUM:x86-64 = "51ee0cb48c6e8c37b45bffd322d08305bca3e8690cd09c12334b610944a41806"
DEVSUM = "a1b018be5958b7369a4134dbad1faea56d3bbc9ffb107fef2634258010efc296"
DEVSUM:x86-64 = "b350348c17bd2354d99c9fa8fe338ed0ef25d56f8db0b0d35a8fb4da1a8f7d8e"

# XXX -
#  The RDMA support has runtime requirements on RDMA/Infiband
#  libraries that we don't have recipes for in OE-Core or here.
# - XXX
do_install:append() {
    rm -rf ${D}${prefix}/local/cuda-${CUDA_VERSION}/gds
    rm -f ${D}${prefix}/local/cuda-${CUDA_VERSION}/lib/libcufile_rdma*
}

BBCLASSEXTEND = "native nativesdk"
