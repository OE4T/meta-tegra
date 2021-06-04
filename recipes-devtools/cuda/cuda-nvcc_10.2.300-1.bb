CUDA_PKG = "${BPN}"

require cuda-shared-binaries-${PV}.inc

MAINSUM = "86c138d6903fa35bb512414bbb98dfd519d3e5ee743dc846e9fb1aa42f7e0391"
MAINSUM_x86-64 = "573d0fed9398a024ba0a0f6aeeb796a13535b227de6b95de22673ab76853df55"

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
