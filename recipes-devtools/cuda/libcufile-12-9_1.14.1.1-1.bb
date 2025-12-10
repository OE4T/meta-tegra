CUDA_PKG = "libcufile libcufile-dev"

require cuda-shared-binaries-12.9.inc

MAINSUM = "e09b9a56a582001a68d6ac3cbdd4228ee8ef5fcc9e11c02aa5fae8613389fb0e"
MAINSUM:x86-64 = "f7d7ce3cdf35b7a0cab2ff7925f9f918f72f579365731c98f5dffd4e607c0f10"
DEVSUM = "6bbccb6751b6273414d2fb7f9de5a44d2c0639f0179ed366d0b9190bf44cb268"
DEVSUM:x86-64 = "40f9ca844eaea14d5c67c15ddaf31330df2e7d828dfe513c96eace63d708626d"

# XXX -
#  The RDMA support has runtime requirements on RDMA/Infiband
#  libraries that we don't have recipes for in OE-Core or here.
# - XXX
do_install:append() {
    rm -rf ${D}${prefix}/local/cuda-${CUDA_VERSION}/gds
    rm -f ${D}${prefix}/local/cuda-${CUDA_VERSION}/lib/libcufile_rdma*
}

BBCLASSEXTEND = "native nativesdk"
