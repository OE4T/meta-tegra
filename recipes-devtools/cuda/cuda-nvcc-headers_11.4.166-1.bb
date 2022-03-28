CUDA_PKG = "cuda-nvcc"

require cuda-shared-binaries.inc

L4T_DEB_GROUP = "cuda-nvcc"
MAINSUM = "9da644cf9ea45f918ab485e5c5dfac76bee2928d7ccb14f40a14d035193c98b2"
MAINSUM:x86-64 = "2ecf7e58481c7f55374c1b7eb17022519ab1fe12a1df85ce9fc13c78d1e06d59"

do_install:append() {
    for d in bin lib nvvm nvvmx; do
        rm -rf ${D}${prefix}/local/cuda-${CUDA_VERSION}/$d
    done
}

CONTAINER_CSV_FILES = ""
CONTAINER_CSV_PKGNAME = ""
FILES:${PN} = "${prefix}/local/cuda-${CUDA_VERSION}/include"
FILES:${PN}-dev = ""

BBCLASSEXTEND = "native nativesdk"
