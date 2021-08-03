DEPENDS = "cuda-driver cuda-misc-headers cuda-nvcc-headers"

require cuda-shared-binaries-${PV}.inc

MAINSUM = "2a718596cf1162bf0076d4ec6db52a5f7c3617b7ab7cd243887376e841a99915"
MAINSUM:x86-64 = "fde9942850342aa5fcba1bef7922e15033f046fb7d7243743d13f284206a4517"
DEVSUM = "5aa2bf1e8e9d467dacbff778b0d2d4a7bd31077a443b7a49711cc798562ea37d"
DEVSUM:x86-64 = "0d8e97b450685b6013ed33f0b96b1c80fad6e813721561a8b5fd83712a678a75"

inherit container-runtime-csv siteinfo
CONTAINER_CSV_FILES = "${sysconfdir}/ld.so.conf.d/cuda-${CUDA_VERSION_DASHED}.conf"

do_compile:append() {
    echo "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}" > ${B}/cuda-${CUDA_VERSION_DASHED}.conf
    if [ "${baselib}" != "lib64" -a "${SITEINFO_BITS}" = "64" ]; then
	if [ -e ${B}/usr/local/cuda-${CUDA_VERSION}/${baselib} ]; then
            ln -s ${baselib} ${B}/usr/local/cuda-${CUDA_VERSION}/lib64
	fi
    fi
}

do_install:append:class-target() {
    install -d ${D}${sysconfdir}/ld.so.conf.d
    install -m 0644 ${B}/cuda-${CUDA_VERSION_DASHED}.conf ${D}${sysconfdir}/ld.so.conf.d/
}

FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/*.a \
                    ${@' ${prefix}/local/cuda-${CUDA_VERSION}/lib64' if d.getVar('baselib') != 'lib64' and d.getVar('SITEINFO_BITS') == '64' else ''}"
FILES:${PN}-staticdev = ""
INSANE_SKIP:${PN}-dev += "staticdev"
RDEPENDS:${PN}-dev:append:class-target = " cuda-nvcc-headers cuda-target-environment"
BBCLASSEXTEND = "native nativesdk"
