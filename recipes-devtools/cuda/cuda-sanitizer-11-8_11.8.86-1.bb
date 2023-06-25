CUDA_PKG="cuda-sanitizer"
L4T_DEB_GROUP = "cuda-sanitizer-api"

require cuda-shared-binaries-11.8.inc

MAINSUM = "c75d6c44a69068224e97c56636be5226fbae13a413711d7428ae7c64ba3e8da3"
MAINSUM:x86-64 = "5b1a177f8986554a336927eaca0a2033e0bbd0af32f9f9aa6dd9f489bba64340"

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
