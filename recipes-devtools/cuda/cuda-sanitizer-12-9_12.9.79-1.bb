CUDA_PKG = "cuda-sanitizer"
L4T_DEB_GROUP = "cuda-sanitizer-api-12-9"

require cuda-shared-binaries-12.9.inc

MAINSUM = "cc43cb7c452e325f4b71a980f12881da87f4d0534084780a9aeeb80f75f180e2"
MAINSUM:x86-64 = "3b2fdf2c3caf82cddc4c4c0f93b3877a5671de04ab7778f9e7d517e497a05218"

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
