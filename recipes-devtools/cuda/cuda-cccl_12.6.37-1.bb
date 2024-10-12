CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "0518e4f42f59d8fcc9081c331cce28b87494f2d66ae3d88a6bec35aa4fdb89d1"
MAINSUM:x86-64 = "2a10b3d387da9407977dcb00e1b7f808ccc0c18a8c0f615fbe81e109dd734edd"

FILES:${PN} = " \
    ${prefix}/local/cuda-${CUDA_VERSION}/include \
    ${prefix}/local/cuda-${CUDA_VERSION}/lib \
"
FILES:${PN}-dev = ""

BBCLASSEXTEND = "native nativesdk"
