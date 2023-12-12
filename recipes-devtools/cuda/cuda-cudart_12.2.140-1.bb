DEPENDS = "cuda-driver cuda-nvcc-headers cuda-cccl cuda-crt"

require cuda-shared-binaries.inc

MAINSUM = "c7dcedc64cc72bb2cba7b57effcd195d648cbc6b550c4bc09e9f32676d5b3f05"
MAINSUM:x86-64 = "15c39957a226919d0c25507e57e845ad78d3f0e72d97c18527952e98484ae99a"
DEVSUM = "fa2f61fd0f29b67706827666486fe4cdbe1b3ca6edd4f608a9cf430249ac236e"
DEVSUM:x86-64 = "1b2842b7d47f4a64d5e07ebec538094815f5f2f23984b22fc603f206830e173c"

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
RDEPENDS:${PN}-dev:append:class-target = " cuda-nvcc-headers cuda-cccl cuda-target-environment cuda-crt"
BBCLASSEXTEND = "native nativesdk"
