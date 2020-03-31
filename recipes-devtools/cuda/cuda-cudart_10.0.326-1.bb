DEPENDS = "cuda-driver cuda-misc-headers"

require cuda-shared-binaries-${PV}.inc

inherit container-runtime-csv siteinfo
CONTAINER_CSV_FILES = "${sysconfdir}/ld.so.conf.d/cuda-${CUDA_VERSION_DASHED}.conf"

do_compile_append() {
    if [ "${baselib}" != "lib64" -a "${SITEINFO_BITS}" = "64" ]; then
	if [ -e ${B}/usr/local/cuda-${CUDA_VERSION}/${baselib} ]; then
            ln -s ${baselib} ${B}/usr/local/cuda-${CUDA_VERSION}/lib64
	fi
    fi
}

do_compile_append_class-target() {
    echo "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}" > ${B}/cuda-${CUDA_VERSION_DASHED}.conf
cat >> ${B}/cuda_environment_setup.sh <<EOF
export CUDA_NVCC_ARCH_FLAGS="${CUDA_NVCC_ARCH_FLAGS}"
EOF
}

do_install_append_class-target() {
    install -d ${D}${sysconfdir}/ld.so.conf.d
    install -m 0644 ${B}/cuda-${CUDA_VERSION_DASHED}.conf ${D}${sysconfdir}/ld.so.conf.d/
    install -d ${D}/environment-setup.d
    install -m 0644 ${B}/cuda_environment_setup.sh ${D}/environment-setup.d/cuda_target.sh
}

FILES_${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/*.a \
                    ${@' ${prefix}/local/cuda-${CUDA_VERSION}/lib64' if d.getVar('baselib') != '64' and d.getVar('SITEINFO_BITS') == '64' else ''}"
FILES_${PN}-staticdev = ""
INSANE_SKIP_${PN}-dev += "staticdev"
FILES_${PN}-dev_append_class-target = " /environment-setup.d"
BBCLASSEXTEND = "native nativesdk"
