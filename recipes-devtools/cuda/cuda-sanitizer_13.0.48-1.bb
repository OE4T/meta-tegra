CUDA_PKG = "${BPN}"
L4T_DEB_GROUP = "cuda-sanitizer-api"

require cuda-shared-binaries.inc

MAINSUM = "5385dc3d3177e5eb8caf62939eb60de8e0606e453be7369e438e8997ced4cbd0"
MAINSUM:x86-64 = "5da503e34a89632ba55bcc902cc88d3a0c1824b0a5420203eb4c7896ef40c3c8"

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
