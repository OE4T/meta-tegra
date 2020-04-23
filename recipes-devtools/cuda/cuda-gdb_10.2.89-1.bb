require cuda-shared-binaries-${PV}.inc

DEPENDS = "ncurses expat"

do_compile_append() {
    sed -i -r -e 's,^(\s*)print (.*)$,\1print(\2),' ${B}/usr/local/cuda-${CUDA_VERSION}/share/gdb/system-gdbinit/*.py
}

FILES_${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/share/gdb"
RDEPENDS_${PN}-dev += "python3"
INSANE_SKIP_${PN}-dev += "staticdev"
BBCLASSEXTEND = "native nativesdk"
