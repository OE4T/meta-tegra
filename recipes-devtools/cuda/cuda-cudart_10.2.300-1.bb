DEPENDS = "cuda-driver cuda-nvcc-headers"

require cuda-shared-binaries-${PV}.inc

MAINSUM = "e3cd683965f7b2e4a13b27c58754443185dcc545d0f989e52c224840cfde48d1"
MAINSUM:x86-64 = "32f3f91e38feb3c2909aab7055de0177441af429f9272d7beb63632528542cc2"
DEVSUM = "d37a94a3fb858db2cf41cde1bcbe1042b9a66d4fd3fd30882805a478523acb18"
DEVSUM:x86-64 = "c006853dec4b26871edaa859a7bcff15aed39142dc5a529262793594a8646e28"

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
RDEPENDS:${PN}-dev:append:class-target = " cuda-nvcc-headers cuda-target-environment"
BBCLASSEXTEND = "native nativesdk"
