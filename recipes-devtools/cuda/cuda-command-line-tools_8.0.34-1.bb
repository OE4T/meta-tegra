require cuda-shared-binaries-${PV}.inc

do_compile_append() {
    if [ "${baselib}" != "lib64" ]; then
        mv ${B}/usr/local/cuda-8.0/extras/CUPTI/lib64 ${B}/usr/local/cuda-8.0/extras/CUPTI/${baselib}
    fi
}

PACKAGES =+ "${PN}-libcupti"
FILES_${PN}-libcupti = "${prefix}/local/cuda-8.0/extras/CUPTI/${baselib}/*${SOLIBS}"
FILES_${PN} += "${prefix}/local/cuda-8.0/bin"
FILES_${PN}-dev += "${prefix}/local/cuda-8.0/extras ${prefix}/local/cuda-8.0/share ${prefix}/local/cuda-8.0/tools"
RDEPENDS_${PN}-dev += "python"
