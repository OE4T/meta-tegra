DEPENDS = "cuda-driver cuda-nvcc-headers cuda-cccl cuda-crt"
CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

MAINSUM = "d29c8f33d6b663f9b1fa4accc9bc0ca508012ebeea5f9b74f8e552688ec1268f"
MAINSUM:x86-64 = "87f96b0e522fd4ffa3b451b803ffd5d5fb70cf81c5b497e828c3d80d345630de"
DEVSUM = "1db5be4f3d008493cc7a10e92839b07551e741d15a67161e0b751b3e2ebc2690"
DEVSUM:x86-64 = "498e6d128834850c309d09c2996e856c3dd0549c3aad7f10780375fdd40d427d"

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
