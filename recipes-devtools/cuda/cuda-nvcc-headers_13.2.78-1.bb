CUDA_PKG = "cuda-nvcc"
CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

L4T_DEB_GROUP = "cuda-nvcc"
MAINSUM = "3b943b0c907d6c56aa8924d9aabd124813c8a329f7f3a836198e0757fc835a15"
MAINSUM:x86-64 = "b847731f2e4ab29858f7490d7b0d1af33fc119c8191f07f44181f204fbc14d96"

do_install:append() {
    for d in bin lib nvvm nvvmx; do
        rm -rf ${D}${prefix}/local/cuda-${CUDA_VERSION}/$d
    done
}

FILES:${PN} = "${prefix}/local/cuda-${CUDA_VERSION}/include"
FILES:${PN}-dev = ""

BBCLASSEXTEND = "native nativesdk"
