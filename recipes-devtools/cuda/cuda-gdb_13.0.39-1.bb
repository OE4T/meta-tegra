CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "5cf033b19a843c8480e2d3665925156f51cd8743cf2c86255cc0218bc06e6973"
MAINSUM:x86-64 = "7ee90a170cfa8fb3040af2b163bf9cd37f699d81630e90749c149b5bcc96851f"

DEPENDS = "ncurses expat"

do_compile:append() {
    sed -i -r -e 's,^(\s*)print (.*)$,\1print(\2),' ${B}/usr/local/cuda-${CUDA_VERSION}/share/gdb/system-gdbinit/*.py
    rm -f ${B}/usr/local/cuda-${CUDA_VERSION}/bin/cuda-gdb-python*-tui
}

FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/share/gdb"
RDEPENDS:${PN} += "gmp"
RDEPENDS:${PN}-dev += "python3"
INSANE_SKIP:${PN}-dev += "staticdev"
BBCLASSEXTEND = "native nativesdk"
