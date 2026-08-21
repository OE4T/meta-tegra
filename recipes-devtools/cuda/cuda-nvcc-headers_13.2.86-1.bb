CUDA_PKG = "cuda-nvcc"

require cuda-shared-binaries.inc

L4T_DEB_GROUP = "cuda-nvcc"
MAINSUM = "f142c71a5dea49fd1f58a97d6899ddf2b80b1cd61da2c5c43eecfb8308121715"
MAINSUM:x86-64 = "5fbb98fe3a1a652bd725104f4099613298cc27c0edc1cd59b20d3c29c756f5ad"

do_install:append() {
    for d in bin lib nvvm nvvmx; do
        rm -rf ${D}${prefix}/local/cuda-${CUDA_VERSION}/$d
    done
}

FILES:${PN} = "${prefix}/local/cuda-${CUDA_VERSION}/include"
FILES:${PN}-dev = ""

BBCLASSEXTEND = "native nativesdk"
