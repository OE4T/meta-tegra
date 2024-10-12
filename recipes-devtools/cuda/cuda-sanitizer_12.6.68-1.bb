CUDA_PKG = "${BPN}"
L4T_DEB_GROUP = "cuda-sanitizer-api"

require cuda-shared-binaries.inc

MAINSUM = "9003f6266f6169b3c52eb2d5fa7f9114bc98048de9bd8a55f2574d41ebd3bed6"
MAINSUM:x86-64 = "63803e9ddf0714f82e934341ae303ec74f82440f185574a4b0621d2a47010034"

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
