CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

MAINSUM = "24a8e9388124a45408a9b5b4ab0368bf8c869c71777bcf7c2d9b1717294b0eda"
MAINSUM:x86-64 = "1e8ea18c8f00f0ca6dc02ed1714d1bb89c546b7c64ced4b493404156737828b0"
DEVSUM = "951f1f8198300a2cfefd9f94ab7c684edd8500c4474ec29dc3af7b37e33d2d30"
DEVSUM:x86-64 = "9429c1f0c5855bc98607347d4c068f3494a4485cef74a1a251b5a72c4f0c64ed"

# XXX -
#  The RDMA support has runtime requirements on RDMA/Infiband
#  libraries that we don't have recipes for in OE-Core or here.
# - XXX
do_install:append() {
    rm -rf ${D}${prefix}/local/cuda-${CUDA_VERSION}/gds
    rm -f ${D}${prefix}/local/cuda-${CUDA_VERSION}/lib/libcufile_rdma*
}

BBCLASSEXTEND = "native nativesdk"
