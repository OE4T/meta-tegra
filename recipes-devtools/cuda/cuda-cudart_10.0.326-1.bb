DEPENDS = "cuda-driver cuda-misc-headers"

require cuda-shared-binaries-${PV}.inc

inherit container-runtime-csv
CONTAINER_CSV_FILES = "${sysconfdir}/ld.so.conf.d/cuda-10-0.conf"

do_compile_append() {
    echo "${prefix}/local/cuda-10.0/${baselib}" > ${B}/cuda-10-0.conf
}

do_install_append() {
    install -d ${D}${sysconfdir}/ld.so.conf.d
    install -m 0644 ${B}/cuda-10-0.conf ${D}${sysconfdir}/ld.so.conf.d/
}
