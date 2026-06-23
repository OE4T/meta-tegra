CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "8967748ac6ee8e4a009bd572976649ff1cf9b44296467c5875fffcec7d964d21"
MAINSUM:x86-64 = "68ce52584e8a1c38e29e1260b5e2c171d6cba319d5f07421e047de19a2e878e3"

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
