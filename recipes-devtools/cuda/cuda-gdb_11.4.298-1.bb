CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "a70509942dc4f9546c605de1556cbea2c80559d5f2e296c7525fa86dc411bcc1"
MAINSUM:x86-64 = "1a8594487d3a82a699fa1398a7ec926c530e828e604191ea1a16b512eb4de634"

DEPENDS = "ncurses expat"

do_compile:append() {
    sed -i -r -e 's,^(\s*)print (.*)$,\1print(\2),' ${B}/usr/local/cuda-${CUDA_VERSION}/share/gdb/system-gdbinit/*.py
}

FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/share/gdb"
RDEPENDS:${PN}-dev += "python3"
INSANE_SKIP:${PN}-dev += "staticdev"
BBCLASSEXTEND = "native nativesdk"
