CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "61884495b69b9c3be080721335088b5cc2b8250521e6b0115a3897c98a10d710"
MAINSUM:x86-64 = "1bbe77b5479f39ac2fa4529753f8e7f5281d67e32565cebd157fa95128e8a9fd"

DEPENDS = "ncurses expat"

do_compile:append() {
    sed -i -r -e 's,^(\s*)print (.*)$,\1print(\2),' ${B}/usr/local/cuda-${CUDA_VERSION}/share/gdb/system-gdbinit/*.py
}

FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/share/gdb"
RDEPENDS:${PN}-dev += "python3"
INSANE_SKIP:${PN}-dev += "staticdev"
BBCLASSEXTEND = "native nativesdk"
