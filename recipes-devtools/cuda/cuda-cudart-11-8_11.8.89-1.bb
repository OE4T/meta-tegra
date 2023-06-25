CUDA_PKG = "cuda-cudart cuda-cudart-dev"

DEPENDS = "cuda-driver-11-8 cuda-nvcc-headers-11-8 cuda-cccl-11-8"

require cuda-shared-binaries-11.8.inc

MAINSUM = "ea6cecee948b673a9c49d9d94d160f2ad4a970a55e3a60994649ad8c9df1efbd"
MAINSUM:x86-64 = "4f603955b63d228e980f14f32dbdabcfa154f4b9659dcc222b746a6c4f450239"
DEVSUM = "05811c05c35753de542b748dedfda651d9f53cc060b0115441a360b8b2b0775c"
DEVSUM:x86-64 = "c3ad62dbe3e80a7ba09fb9a69e5819abb274bb398fdcffbd7c7d8fa583dd700d"

inherit siteinfo

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

FILES:${PN}-dev += " \
    ${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/*.a \
    ${@' ${prefix}/local/cuda-${CUDA_VERSION}/lib64' if d.getVar('baselib') != 'lib64' and d.getVar('SITEINFO_BITS') == '64' else ''} \
"
FILES:${PN}-staticdev = ""
INSANE_SKIP:${PN}-dev += "staticdev"
RDEPENDS:${PN}-dev:append:class-target = " cuda-nvcc-headers-11-8 cuda-cccl-11-8 cuda-target-environment"
BBCLASSEXTEND = "native nativesdk"
