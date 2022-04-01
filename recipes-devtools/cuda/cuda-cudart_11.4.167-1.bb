DEPENDS = "cuda-driver cuda-nvcc-headers cuda-cccl"

require cuda-shared-binaries.inc

MAINSUM = "477177bc8373a33ba4f9461fc21d1f9b108a328d7df16869cf943530b8f852c7"
MAINSUM:x86-64 = "db63396249979a63c4d00636bc2dec023eb0b2c345036568b2ec458a8bf43c18"
DEVSUM = "a0f2ee6413221cd030c4cfec4e8bac86c507eb79f7a2f6651486a80f4f66831f"
DEVSUM:x86-64 = "f7735dc547b74fdb93784621af25bd622ce9743332dfbb79a624ef55411e5e51"

inherit container-runtime-csv siteinfo
CONTAINER_CSV_FILES += " \
    ${sysconfdir}/ld.so.conf.d/cuda-${CUDA_VERSION_DASHED}.conf \
"

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
RDEPENDS:${PN}-dev:append:class-target = " cuda-nvcc-headers cuda-cccl cuda-target-environment"
BBCLASSEXTEND = "native nativesdk"
