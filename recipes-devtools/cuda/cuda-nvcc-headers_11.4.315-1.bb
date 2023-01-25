CUDA_PKG = "cuda-nvcc"

require cuda-shared-binaries.inc

L4T_DEB_GROUP = "cuda-nvcc"
MAINSUM = "17899cc7c04d3766f768c5a41094d09c393043165efe86732ee09ea4f83479cc"
MAINSUM:x86-64 = "c6af78a9f24aea33dfcbe1fef280a40062688a749418f75fcbf164c770c4e764"

do_install:append() {
    for d in bin lib nvvm nvvmx; do
        rm -rf ${D}${prefix}/local/cuda-${CUDA_VERSION}/$d
    done
}

FILES:${PN} = "${prefix}/local/cuda-${CUDA_VERSION}/include"
FILES:${PN}-dev = ""

BBCLASSEXTEND = "native nativesdk"
