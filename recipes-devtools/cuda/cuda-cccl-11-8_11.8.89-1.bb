CUDA_PKG = "cuda-cccl"
L4T_DEB_GROUP = "cuda-thrust"

require cuda-shared-binaries-11.8.inc

MAINSUM = "6e2c7702dfbb2ddc3ef2e291b26c3d993a6ef5e382a8ba78bf6d50b9b57feab8"
MAINSUM:x86-64 = "38dc61ecf5059b8b22dc7cae2e5aa1f09263b8b9295eb225f69b3d4d76778c53"

do_install:append() {
    rm -rf ${D}${prefix}/local/cuda-${CUDA_VERSION}/lib
}

FILES:${PN} = "${prefix}/local/cuda-${CUDA_VERSION}/include"
FILES:${PN}-dev = ""

BBCLASSEXTEND = "native nativesdk"
