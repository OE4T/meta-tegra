CUDA_PKG = "cuda-gdb"

require cuda-shared-binaries-12.9.inc

MAINSUM = "9356b3e9ae78ba0a7cefbc81ce0623916b6ee4d85267011d79da36602476e99c"
MAINSUM:x86-64 = "f3a7d07c68a70fbbfda5574bd1fc0d151c682bae386c0be6d708afdc0b94980f"

DEPENDS = "ncurses expat"

do_compile:append() {
    sed -i -r -e 's,^(\s*)print (.*)$,\1print(\2),' ${B}/usr/local/cuda-${CUDA_VERSION}/share/gdb/system-gdbinit/*.py
}

FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/share/gdb"
RDEPENDS:${PN} += "gmp"
RDEPENDS:${PN}-dev += "python3"
INSANE_SKIP:${PN}-dev += "staticdev"
BBCLASSEXTEND = "native nativesdk"
