CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

SRC_URI:append = " file://0001-Avoid-compile-issues-with-libc-math-functions.patch"
SRC_URI:append = " file://0001-Fix-invalid-C-syntax-in-proclaims_copyable_arguments.patch"

MAINSUM = "9f68cd3661fab32a7521eb598bbf0ea1a7bc062ab231c6fbd886ccd738dd0156"
MAINSUM:x86-64 = "51f09a280239cc803773f6f7e5fc6d9579a11b6a8dcf20d7bf860a8dbb7b4468"

FILES:${PN} = " \
    ${prefix}/local/cuda-${CUDA_VERSION}/include \
    ${prefix}/local/cuda-${CUDA_VERSION}/lib \
"
FILES:${PN}-dev = ""

BBCLASSEXTEND = "native nativesdk"
