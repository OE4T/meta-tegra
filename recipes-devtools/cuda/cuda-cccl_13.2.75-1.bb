CUDA_PKG = "${BPN}"
CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

SRC_URI:append = " file://0001-Avoid-compile-issues-with-libc-math-functions.patch"

MAINSUM = "949fde97f8b10c7ee0306c02cf7bfbeed2faef8acfccc10ad54af69bb90cb181"
MAINSUM:x86-64 = "0c8259dc86abaffcb588b7f98a662d7b5c9064d16049ee591c1f07949640444d"

FILES:${PN} = " \
    ${prefix}/local/cuda-${CUDA_VERSION}/include \
    ${prefix}/local/cuda-${CUDA_VERSION}/lib \
"
FILES:${PN}-dev = ""

BBCLASSEXTEND = "native nativesdk"
