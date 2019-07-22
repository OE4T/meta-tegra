CUDA_PKG = "cuda-gdb cuda-nvprof cuda-memcheck cuda-nvdisasm cuda-cupti cuda-gpu-library-advisor cuda-nvtx"

require cuda-shared-binaries-${PV}.inc

DEPENDS = "ncurses expat"

do_compile_append() {
    if [ "${baselib}" != "lib64" ]; then
        mv ${B}/usr/local/cuda-10.0/extras/CUPTI/lib64 ${B}/usr/local/cuda-10.0/extras/CUPTI/${baselib}
    fi
}

PACKAGES =+ "${PN}-libcupti ${PN}-libcupti-dev ${PN}-libnvtoolsext ${PN}-libnvtoolsext-dev"
FILES_${PN}-libnvtoolsext = "${prefix}/local/cuda-10.0/lib/libnvToolsExt${SOLIBS}"
FILES_${PN}-libnvtoolsext-dev = "${prefix}/local/cuda-10.0/lib/libnvToolsExt${SOLIBSDEV}"
INSANE_SKIP_${PN}-libnvtoolsext = "ldflags libdir"
INSANE_SKIP_${PN}-libnvtoolsext-dev = "ldflags libdir dev-elf"
FILES_${PN}-libcupti = "${prefix}/local/cuda-10.0/extras/CUPTI/${baselib}/*${SOLIBS}"
FILES_${PN}-libcupti-dev = "${prefix}/local/cuda-10.0/extras/CUPTI"
INSANE_SKIP_${PN}-libcupti = "ldflags libdir"
INSANE_SKIP_${PN}-libcupti-dev = "ldflags libdir dev-elf"
FILES_${PN} += "${prefix}/local/cuda-10.0/bin"
FILES_${PN}-dev += "${prefix}/local/cuda-10.0/extras ${prefix}/local/cuda-10.0/share ${prefix}/local/cuda-10.0/tools"
RDEPENDS_${PN}-dev += "python"
INSANE_SKIP_${PN}-dev += "staticdev"
