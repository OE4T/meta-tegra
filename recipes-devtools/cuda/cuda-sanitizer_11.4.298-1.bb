CUDA_PKG = "${BPN}"
L4T_DEB_GROUP = "cuda-sanitizer-api"

require cuda-shared-binaries.inc

MAINSUM = "5fbc7a7c778a387730003522c9442f1d321a0b41317529f47dd7dd5bf9f1f92c"
MAINSUM:x86-64 = "0fe650b5d707c91f55f86d55a659c6dc9178579dd9e80d9cfbd92dc595bd03b3"

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
