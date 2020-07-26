CUDA_PKG = "${BPN}"

require cuda-shared-binaries-${PV}.inc

SRC_URI[main.sha256sum] = "1a0ea57d4c1b1d9394d7e4f6ab94baa2aa49883f4ba2d59a60b750bb88d0fdeb"

# header files are populated by cuda-nvcc-headers recipes
do_install_append() {
    rm -rf ${D}${prefix}/local/cuda-${CUDA_VERSION}/include
}

FILES_${PN} = "${prefix}/local/cuda-${CUDA_VERSION}"
FILES_${PN}-dev = ""
INSANE_SKIP_${PN} += "dev-so"
RDEPENDS_${PN} = "${BPN}-headers"
RDEPENDS_${PN}_append_class-nativesdk = " nativesdk-cuda-environment"

BBCLASSEXTEND = "native nativesdk"
