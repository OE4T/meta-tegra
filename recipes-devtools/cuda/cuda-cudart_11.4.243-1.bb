DEPENDS = "cuda-driver cuda-nvcc-headers cuda-cccl"

require cuda-shared-binaries.inc

MAINSUM = "5b1124506d3ebf10b92e80da25529402f61ac63fa5903082571ce52e7114272a"
MAINSUM:x86-64 = "cfaf9b30c5078c57fd9cf8590266c895fb37a659600757a55f6bbf910ec483b0"
DEVSUM = "80d56fc569c73163fdbc94068a2941ca1622bc7b06da872f33408567b06d5cec"
DEVSUM:x86-64 = "f33d271bd7fc1b8d3d9fb5a535585eee0edf531f07167d0af0bca0524e687ff1"

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
