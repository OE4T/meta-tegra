CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "db4c0ab18446e05a62c8f001c26327cc76d7988edc850ad1bff0661792d3afe5"
MAINSUM:x86-64 = "a589879c8eef37ef59fdd06fdf907f9363d92c864dd05bf0588500973a7a50f6"

DEPENDS = "ncurses expat"

do_compile:append() {
    sed -i -r -e 's,^(\s*)print (.*)$,\1print(\2),' ${B}/usr/local/cuda-${CUDA_VERSION}/share/gdb/system-gdbinit/*.py
}

FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/share/gdb"
RDEPENDS:${PN} += "gmp"
RDEPENDS:${PN}-dev += "python3"
INSANE_SKIP:${PN}-dev += "staticdev"
BBCLASSEXTEND = "native nativesdk"
