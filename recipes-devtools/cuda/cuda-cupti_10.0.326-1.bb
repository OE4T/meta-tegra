require cuda-shared-binaries-${PV}.inc

do_compile_append() {
    if [ "${baselib}" != "lib64" ]; then
        mv ${B}/usr/local/cuda-10.0/extras/CUPTI/lib64 ${B}/usr/local/cuda-10.0/extras/CUPTI/${baselib}
    fi
}

FILES_${PN} = "${prefix}/local/cuda-10.0/extras/CUPTI/${baselib}/*${SOLIBS}"
FILES_${PN}-dev = "${prefix}/local/cuda-10.0/extras/CUPTI/Readme.txt ${prefix}/local/cuda-10.0/extras/CUPTI/${baselib}/*${SOLIBSDEV} \
                   ${prefix}/local/cuda-10.0/extras/CUPTI/include ${prefix}/local/cuda-10.0/extras/CUPTI/sample"
INSANE_SKIP_${PN} = "ldflags libdir"
INSANE_SKIP_${PN}-dev = "ldflags libdir dev-elf"

BBCLASSEXTEND = "native nativesdk"
