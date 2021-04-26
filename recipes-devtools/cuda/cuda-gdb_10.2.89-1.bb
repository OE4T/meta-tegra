CUDA_PKG = "${BPN}"

require cuda-shared-binaries-${PV}.inc

SRC_URI[main.sha256sum] = "d3fef316b7d5b215cf11ff0190c69d9e8a2652b9dd37f454c7350110954e3496"

DEPENDS = "ncurses expat"

do_compile_append() {
    sed -i -r -e 's,^(\s*)print (.*)$,\1print(\2),' ${B}/usr/local/cuda-${CUDA_VERSION}/share/gdb/system-gdbinit/*.py
}

FILES_${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/share/gdb"
RDEPENDS_${PN}-dev += "python3"
INSANE_SKIP_${PN}-dev += "staticdev"
BBCLASSEXTEND = "native nativesdk"
