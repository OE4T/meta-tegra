CUDA_PKG = "cuda-cudart cuda-cudart-dev"

DEPENDS = "cuda-driver-12-9 cuda-nvcc-headers-12-9 cuda-cccl-12-9 cuda-crt-12-9"

require cuda-shared-binaries-12.9.inc

MAINSUM = "a01ed9953693fa52a1ec83386d7cacf91818217d819ab4747f0ec45ac47e9854"
MAINSUM:x86-64 = "2649df5457dcf9a741f979b5c71e09aba563a11ef1c84a0725c3fe7a37625e6e"
DEVSUM = "9de59d51abf4ea2942967869d263f22718651a3e58534b6d0afdc9c012dc342d"
DEVSUM:x86-64 = "e3ca8561b6bec2a92928ebafd68ed9278bf66d5521a2584d6370792386b247c0"


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
