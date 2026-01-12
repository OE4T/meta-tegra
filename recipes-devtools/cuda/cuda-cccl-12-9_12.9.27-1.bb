CUDA_PKG = "cuda-cccl"

require cuda-shared-binaries-12.9.inc

MAINSUM = "36349345a9e20d48fb2d202320ea1710223e732f9220b3c039531555cec3b77c"
MAINSUM:x86-64 = "255b92bd5e09cd10aedaf098d1ee0f66ca1a9062efe96e2e1f385583ddf18c73"

FILES:${PN} = " \
    ${prefix}/local/cuda-${CUDA_VERSION}/include \
    ${prefix}/local/cuda-${CUDA_VERSION}/lib \
"
FILES:${PN}-dev = ""

BBCLASSEXTEND = "native nativesdk"
