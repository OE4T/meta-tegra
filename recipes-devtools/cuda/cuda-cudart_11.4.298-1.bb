DEPENDS = "cuda-driver cuda-nvcc-headers cuda-cccl"

require cuda-shared-binaries.inc

MAINSUM = "488274208dc7bc2c0b0c803277926510b5e586ecc2b239a695978158da191ccd"
MAINSUM:x86-64 = "449252b787a7a376c3e7f062fb7160360067a59e5a77ff48acdb3377ff404032"
DEVSUM = "fbeb7d4ea372feaeaed88780490981cd239ae3a3fcf63bc83d87a7288902b9ba"
DEVSUM:x86-64 = "06ed0856400bbfb5911b4e8eb69984c7c6947d944fd08a512df60c748a7d723a"

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

FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/*.a \
                    ${@' ${prefix}/local/cuda-${CUDA_VERSION}/lib64' if d.getVar('baselib') != 'lib64' and d.getVar('SITEINFO_BITS') == '64' else ''}"
FILES:${PN}-staticdev = ""
INSANE_SKIP:${PN}-dev += "staticdev"
RDEPENDS:${PN}-dev:append:class-target = " cuda-nvcc-headers cuda-cccl cuda-target-environment"
BBCLASSEXTEND = "native nativesdk"
