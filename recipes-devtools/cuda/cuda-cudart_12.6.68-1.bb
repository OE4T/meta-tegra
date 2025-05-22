DEPENDS = "cuda-driver cuda-nvcc-headers cuda-cccl cuda-crt"

require cuda-shared-binaries.inc

MAINSUM = "54456ed3372670d6a6d7e19d5971ec234800fe620dfb822aa751bc7d80f2a516"
MAINSUM:x86-64 = "3b8ffb1d32d8229b248e1b2e4b4dcac94347b73b452b985e762e25514b15b7f5"
DEVSUM = "2ddcaeec93f2c508533f52bea7ad3f339d223d8724537e4695cf14a034b65193"
DEVSUM:x86-64 = "59c7e210da81a6216706a42fb955385f60de77e0adf372115a6e096017e2f281"


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
RDEPENDS:${PN}-dev:append:class-target = " cuda-nvcc-headers cuda-cccl cuda-target-environment cuda-crt-dev"
BBCLASSEXTEND = "native nativesdk"
