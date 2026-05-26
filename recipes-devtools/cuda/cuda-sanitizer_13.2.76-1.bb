CUDA_PKG = "${BPN}"
L4T_DEB_GROUP = "cuda-sanitizer-api"

require cuda-shared-binaries.inc

MAINSUM = "2cf388d6bd5b3e20ba9825c5c91423c39b3ef623476f9a73ef6a035d88038dd3"
MAINSUM:x86-64 = "8ef1054f748134b67d68c30051474214c327975243088e4f449621ede0ba3071"

do_compile:append() {
    rm -rf ${S}/usr/local/cuda-${CUDA_VERSION}/compute-sanitizer/x86
}

FILES:${PN} += " \
    ${prefix}/local/cuda-${CUDA_VERSION}/compute-sanitizer/TreeLauncherSubreaper \
    ${prefix}/local/cuda-${CUDA_VERSION}/compute-sanitizer/compute-sanitizer \
    ${prefix}/local/cuda-${CUDA_VERSION}/compute-sanitizer/TreeLauncherTargetLdPreloadHelper \
"

FILES:${PN}-dev += " \
    ${prefix}/local/cuda-${CUDA_VERSION}/compute-sanitizer/*${SOLIBSDEV} \
    ${prefix}/local/cuda-${CUDA_VERSION}/compute-sanitizer/include \
"

FILES:${PN}-doc += " \
    ${prefix}/local/cuda-${CUDA_VERSION}/compute-sanitizer/docs \
"

RDEPENDS:${PN} += "bash"

BBCLASSEXTEND = "native nativesdk"
