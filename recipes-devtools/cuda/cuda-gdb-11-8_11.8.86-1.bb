CUDA_PKG="cuda-gdb"

require cuda-shared-binaries-11.8.inc

MAINSUM = "57b13c4e62f07d48bb30268216dc151e9a3fadb803d0a46cb18b234fcd96ebce"
MAINSUM:x86-64 = "692b474c088788a7d3ebb0c72c70c998133fc02158f07a6ad9be5ee32d59fa52"

DEPENDS = "ncurses expat"

do_compile:append() {
    sed -i -r -e 's,^(\s*)print (.*)$,\1print(\2),' ${B}/usr/local/cuda-${CUDA_VERSION}/share/gdb/system-gdbinit/*.py
}

FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/share/gdb"
RDEPENDS:${PN}-dev += "python3"
INSANE_SKIP:${PN}-dev += "staticdev"
BBCLASSEXTEND = "native nativesdk"
