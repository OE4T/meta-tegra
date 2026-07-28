
require cuda-shared-binaries.inc

MAINSUM = "a95cccf448abe7ee199ce22b6e5f9a650ba89d1d7d487bd8774600d96bcad014"
MAINSUM:x86-64 = "3639533f8c01bca604dc432c7491fb8b66de0bc7831a6ccbfd62b3d432cf8e8b"
DEVSUM = "95286ec5eb4865064784779f63fc5c575d44f144845016f6a8886b955d89d516"
DEVSUM:x86-64 = "efd8fc32fa8567aa4af0d3612626f8aaa1dc0bfea3a3e90286f98e9f47d831d2"

# XXX -
#  The RDMA support has runtime requirements on RDMA/Infiband
#  libraries that we don't have recipes for in OE-Core or here.
# - XXX
do_install:append() {
    rm -rf ${D}${prefix}/local/cuda-${CUDA_VERSION}/gds
    rm -f ${D}${prefix}/local/cuda-${CUDA_VERSION}/lib/libcufile_rdma*
}

BBCLASSEXTEND = "native nativesdk"
