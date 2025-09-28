CUDA_PKG = "${BPN}"
CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

SRC_URI:append = " file://0001-Avoid-compile-issues-with-libc-math-functions.patch"

MAINSUM = "4ddff5a943bbce2d1793d28c3cde63fd2b011c9515cc5b008f8955005c2965b4"
MAINSUM:x86-64 = "076ffa2a84ea3e15937136737bbfc174e03616ae29f5800bf5a3aa6552c088b4"

FILES:${PN} = " \
    ${prefix}/local/cuda-${CUDA_VERSION}/include \
    ${prefix}/local/cuda-${CUDA_VERSION}/lib \
"
FILES:${PN}-dev = ""

BBCLASSEXTEND = "native nativesdk"
