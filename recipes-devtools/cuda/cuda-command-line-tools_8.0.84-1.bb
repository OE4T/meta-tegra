require cuda-shared-binaries-${PV}.inc

DEPENDS = "ncurses expat"

do_compile_append() {
    if [ "${baselib}" != "lib64" ]; then
        mv ${B}/usr/local/cuda-8.0/extras/CUPTI/lib64 ${B}/usr/local/cuda-8.0/extras/CUPTI/${baselib}
    fi
}

DEPENDS = "expat"

PACKAGES =+ "${PN}-libcupti ${PN}-libcupti-dev"
FILES_${PN}-libcupti = "${prefix}/local/cuda-8.0/extras/CUPTI/${baselib}/*${SOLIBS}"
FILES_${PN}-libcupti-dev = "${prefix}/local/cuda-8.0/extras/CUPTI"
INSANE_SKIP_${PN}-libcupti = "ldflags libdir"
INSANE_SKIP_${PN}-libcupti-dev = "ldflags libdir dev-elf"
FILES_${PN} += "${prefix}/local/cuda-8.0/bin"
FILES_${PN}-dev += "${prefix}/local/cuda-8.0/extras ${prefix}/local/cuda-8.0/share ${prefix}/local/cuda-8.0/tools"
RDEPENDS_${PN}-dev += "python"
INSANE_SKIP_${PN}-dev += "staticdev"
