CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "4874345b5232d94adb6b15a9e2c310eed62bfb211ed33f934b80d061ecd46e39"
MAINSUM:x86-64 = "77c52a4dcc980368ab412c9c9b0066061a1f7025448a0173002697238fa8486c"

DEPENDS = "ncurses expat"

do_compile:append() {
    sed -i -r -e 's,^(\s*)print (.*)$,\1print(\2),' ${B}/usr/local/cuda-${CUDA_VERSION}/share/gdb/system-gdbinit/*.py
}

FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/share/gdb"
RDEPENDS:${PN}-dev += "python3"
INSANE_SKIP:${PN}-dev += "staticdev"
BBCLASSEXTEND = "native nativesdk"
