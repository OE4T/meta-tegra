CUDA_PKG = "cuda-nvcc"

require cuda-shared-binaries.inc

L4T_DEB_GROUP = "cuda-nvcc"
MAINSUM = "89125374848566a4e6b2044ceb8a90cb0b326f278f6abe74486d4202cfd2bf19"
MAINSUM:x86-64 = "68b9debe1f19b962f413dbc597b7bf5fcb1f36eaa11ce870d02b697b69c0dadd"

do_install:append() {
    for d in bin lib nvvm nvvmx; do
        rm -rf ${D}${prefix}/local/cuda-${CUDA_VERSION}/$d
    done
}

FILES:${PN} = "${prefix}/local/cuda-${CUDA_VERSION}/include"
FILES:${PN}-dev = ""

BBCLASSEXTEND = "native nativesdk"
