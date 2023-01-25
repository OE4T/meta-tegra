CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "17899cc7c04d3766f768c5a41094d09c393043165efe86732ee09ea4f83479cc"
MAINSUM:x86-64 = "c6af78a9f24aea33dfcbe1fef280a40062688a749418f75fcbf164c770c4e764"

# header files are populated by cuda-nvcc-headers recipes
do_install:append() {
    rm -rf ${D}${prefix}/local/cuda-${CUDA_VERSION}/include
}

FILES:${PN} = "${prefix}/local/cuda-${CUDA_VERSION}"
FILES:${PN}-dev = ""
INSANE_SKIP:${PN} += "dev-so"
RDEPENDS:${PN} = "${BPN}-headers"
RDEPENDS:${PN}:append:class-nativesdk = " nativesdk-cuda-environment"

BBCLASSEXTEND = "native nativesdk"
