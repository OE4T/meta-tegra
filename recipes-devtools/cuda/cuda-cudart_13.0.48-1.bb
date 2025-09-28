DEPENDS = "cuda-driver cuda-nvcc-headers cuda-cccl cuda-crt"
CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

MAINSUM = "399365014352921445857edf107863a41bb7c327d56b9b2f15b4a4858c6ad70e"
MAINSUM:x86-64 = "5193e1b0d52a74c852348235c3ea695d8842a0e3d9a8b1607f715de1ea00143e"
DEVSUM = "a250e6f687a78638edbd722846dadd720e6925fad19570c8fea3be14a94c7d70"
DEVSUM:x86-64 = "f70a40dda91e50c7fdf0e137b98440ad0cf42b32f935ab70cdc766d5ac9ef8ce"


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
