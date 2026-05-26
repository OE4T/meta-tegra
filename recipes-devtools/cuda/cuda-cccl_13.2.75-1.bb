CUDA_PKG = "${BPN}"
CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

SRC_URI:append = " file://0001-Avoid-compile-issues-with-libc-math-functions.patch"

MAINSUM = "949fde97f8b10c7ee0306c02cf7bfbeed2faef8acfccc10ad54af69bb90cb181"
MAINSUM:x86-64 = "7fca8d06ad1f527ad35a514bbe4f797a9e292fb9305b313001edd645bd62b7e7"

FILES:${PN} = " \
    ${prefix}/local/cuda-${CUDA_VERSION}/include \
    ${prefix}/local/cuda-${CUDA_VERSION}/lib \
"
FILES:${PN}-dev = ""

BBCLASSEXTEND = "native nativesdk"
