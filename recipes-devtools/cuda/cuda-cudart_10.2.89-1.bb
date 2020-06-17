DEPENDS = "cuda-driver cuda-misc-headers"

require cuda-shared-binaries-${PV}.inc

SRC_URI[main.sha256sum] = "2a718596cf1162bf0076d4ec6db52a5f7c3617b7ab7cd243887376e841a99915"
SRC_URI[dev.sha256sum] = "5aa2bf1e8e9d467dacbff778b0d2d4a7bd31077a443b7a49711cc798562ea37d"

inherit container-runtime-csv siteinfo
CONTAINER_CSV_FILES = "${sysconfdir}/ld.so.conf.d/cuda-${CUDA_VERSION_DASHED}.conf"

do_compile_append() {
    echo "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}" > ${B}/cuda-${CUDA_VERSION_DASHED}.conf
    if [ "${baselib}" != "lib64" -a "${SITEINFO_BITS}" = "64" ]; then
	if [ -e ${B}/usr/local/cuda-${CUDA_VERSION}/${baselib} ]; then
            ln -s ${baselib} ${B}/usr/local/cuda-${CUDA_VERSION}/lib64
	fi
    fi
}

do_install_append_class-target() {
    install -d ${D}${sysconfdir}/ld.so.conf.d
    install -m 0644 ${B}/cuda-${CUDA_VERSION_DASHED}.conf ${D}${sysconfdir}/ld.so.conf.d/
}

FILES_${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/*.a \
                    ${@' ${prefix}/local/cuda-${CUDA_VERSION}/lib64' if d.getVar('baselib') != 'lib64' and d.getVar('SITEINFO_BITS') == '64' else ''}"
FILES_${PN}-staticdev = ""
INSANE_SKIP_${PN}-dev += "staticdev"
RDEPENDS_${PN}-dev_append_class-target = " cuda-target-environment"
BBCLASSEXTEND = "native nativesdk"
