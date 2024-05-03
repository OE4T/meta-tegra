CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

DEPENDS = "cuda-cudart cuda-nvvm cuda-crt"

MAINSUM = "89125374848566a4e6b2044ceb8a90cb0b326f278f6abe74486d4202cfd2bf19"
MAINSUM:x86-64 = "68b9debe1f19b962f413dbc597b7bf5fcb1f36eaa11ce870d02b697b69c0dadd"

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
