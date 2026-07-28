CUDA_PKG = "${BPN}"
L4T_DEB_GROUP = "cuda-sanitizer-api"

require cuda-shared-binaries.inc

MAINSUM = "66d84a129f189da197c26dfc3f53f4db474b2d1b2530c491201deebead46c8fb"
MAINSUM:x86-64 = "c637b1602be37576dac4c2f169bf71ac90d7b4a867df765dcffd151a30d55b8f"

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
