require cuda-shared-binaries.inc

MAINSUM = "d48aadaf52731f2e058a976581e55f0ad18a24dafb020a1324a8c7a293cba5ef"
MAINSUM:x86-64 = "704a0b1be11bf95288dab3eee3854cb5ef9041c65978dbda1f175fe4efb4ec96"
DEVSUM = "a8e59753cab9e361454df3d4408b589e36659fe074ddb249ba6f36a2bfbeeaf7"
DEVSUM:x86-64 = "5e42aa65b4de16335dc0365afe3212131c2ff71fbc6647fea8dc51daf23477d5"

# XXX -
#  The RDMA support has runtime requirements on RDMA/Infiband
#  libraries that we don't have recipes for in OE-Core or here.
# - XXX
do_install:append() {
    rm -rf ${D}${prefix}/local/cuda-${CUDA_VERSION}/gds
    rm -f ${D}${prefix}/local/cuda-${CUDA_VERSION}/lib/libcufile_rdma*
}

BBCLASSEXTEND = "native nativesdk"
