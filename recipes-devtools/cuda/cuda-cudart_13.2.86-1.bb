DEPENDS = "cuda-driver cuda-nvcc-headers cuda-cccl cuda-crt"

require cuda-shared-binaries.inc

MAINSUM = "550a1c3c915637b5c0d6538b9eba87471742c4be854438ab8a050caabdea8001"
MAINSUM:x86-64 = "cc2c8107201f48cab9e0d2a61c768d7c02229bc2d581d9974e09a96e4e2f4364"
DEVSUM = "a9d556536583e12d1662afc930883dd3b3f2a56389081a7d101a44dac1576e45"
DEVSUM:x86-64 = "703efe9664a08de7e89d7eb5dc9080fce680bb149100b0c5e50bd9f72c950cc7"

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
