CUDA_PKG = "cuda-nvcc"

require cuda-shared-binaries-12.9.inc

L4T_DEB_GROUP = "cuda-nvcc"
MAINSUM = "337b9720ba0d71a3da4502a0f82e0f132a5edc7629b04275a191efb131c60617"
MAINSUM:x86-64 = "66c11ad4fba8c12c44a1fec346c5125011e2ed519b98a787966dda0df9043b45"

do_install:append() {
    for d in bin lib nvvm nvvmx; do
        rm -rf ${D}${prefix}/local/cuda-${CUDA_VERSION}/$d
    done
}

FILES:${PN} = "${prefix}/local/cuda-${CUDA_VERSION}/include"
FILES:${PN}-dev = ""

BBCLASSEXTEND = "native nativesdk"
