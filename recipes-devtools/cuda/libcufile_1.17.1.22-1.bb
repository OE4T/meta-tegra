CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

MAINSUM = "6ac612569895fc0ec6f07c6a9d1b382b87629418495aa0d4454c7b2fe7d30ee3"
MAINSUM:x86-64 = "56b3ba9d4dd25d23adf5d6d411c40a77099de90a996fc6bfed8b4d13dc412398"
DEVSUM = "a1b018be5958b7369a4134dbad1faea56d3bbc9ffb107fef2634258010efc296"
DEVSUM:x86-64 = "58c110a4e7e94e865b7b5bd51600ee64c8cfda72aae776a9355c5d3f7d3d47d2"

# XXX -
#  The RDMA support has runtime requirements on RDMA/Infiband
#  libraries that we don't have recipes for in OE-Core or here.
# - XXX
do_install:append() {
    rm -rf ${D}${prefix}/local/cuda-${CUDA_VERSION}/gds
    rm -f ${D}${prefix}/local/cuda-${CUDA_VERSION}/lib/libcufile_rdma*
}

BBCLASSEXTEND = "native nativesdk"
