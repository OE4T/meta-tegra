CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "015b210c2f07b3aa9e3959ecdad070f228aeb8cd62415996426ce9bb37993727"
MAINSUM:x86-64 = "62a389a1d614d65dbea7179022e363befd43afc463fad2c2cc7285a665fa1def"

DEPENDS = "ncurses expat"

do_compile:append() {
    sed -i -r -e 's,^(\s*)print (.*)$,\1print(\2),' ${B}/usr/local/cuda-${CUDA_VERSION}/share/gdb/system-gdbinit/*.py
    rm -f ${B}/usr/local/cuda-${CUDA_VERSION}/bin/cuda-gdb-python*-tui
}

FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/share/gdb"
RDEPENDS:${PN} += "gmp"
RDEPENDS:${PN}-dev += "python3"
INSANE_SKIP:${PN}-dev += "staticdev"
BBCLASSEXTEND = "native"
