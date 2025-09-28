CUDA_PKG = "cuda-nvcc"
CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

L4T_DEB_GROUP = "cuda-nvcc"
MAINSUM = "c320d58169ba86a3adbbd48955efa9d2a62c08009f2bbeca8721317533bc70cc"
MAINSUM:x86-64 = "8b7ebed1b37bf9eda238a3a3fe959812aa79c1c6b2498cb21df144890e01efd6"

do_install:append() {
    for d in bin lib nvvm nvvmx; do
        rm -rf ${D}${prefix}/local/cuda-${CUDA_VERSION}/$d
    done
}

FILES:${PN} = "${prefix}/local/cuda-${CUDA_VERSION}/include"
FILES:${PN}-dev = ""

BBCLASSEXTEND = "native nativesdk"
