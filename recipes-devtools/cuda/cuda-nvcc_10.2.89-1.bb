CUDA_PKG = "${BPN}"

require cuda-shared-binaries-${PV}.inc

MAINSUM = "1a0ea57d4c1b1d9394d7e4f6ab94baa2aa49883f4ba2d59a60b750bb88d0fdeb"
MAINSUM:x86-64 = "a4d649cb1433f7f9e2453abf2caf606819db0eb57def025568f276c31da53f85"

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
