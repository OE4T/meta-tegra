require cuda-shared-binaries-${PV}.inc

DEPENDS = "ncurses expat"

do_compile_append() {
    sed -i -r -e 's,^(\s*)print (.*)$,\1print(\2),' ${B}/usr/local/cuda-10.0/share/gdb/system-gdbinit/*.py
}

FILES_${PN}-dev += "${prefix}/local/cuda-10.0/share/gdb"
RDEPENDS_${PN}-dev += "python3"
INSANE_SKIP_${PN}-dev += "staticdev"
BBCLASSEXTEND = "native nativesdk"
