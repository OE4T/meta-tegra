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
# cuda-gdb ships pre-built TUI binaries for multiple Python versions (3.8-3.12)
# linked against libpython3.x, libncurses and libcrypt from the host OS.
# There are no nativesdk providers for these system libraries — they are
# expected to be satisfied by the developer's host at runtime.
INSANE_SKIP:${PN}:class-nativesdk += "file-rdeps"
BBCLASSEXTEND = "native nativesdk"
