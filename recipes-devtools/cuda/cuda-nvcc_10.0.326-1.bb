require cuda-shared-binaries-${PV}.inc

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
