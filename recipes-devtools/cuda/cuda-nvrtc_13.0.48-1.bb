CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

MAINSUM = "7620bb17b14e57596775d9fd0284a4df618283f0c6772c77de4abfbac2091480"
MAINSUM:x86-64 = "aa7ca951c12b1c64d7ae5e0d7612cbfb1001efa13ec2cc458f2a219ba284fc9f"
DEVSUM = "f7cafa202fe87b32d3192cf3f203b5aa03a62095b9acaf75135ef7f5270f7bbc"
DEVSUM:x86-64 = "e610118ae2c2584b8ef99bd6cf1c4b589d0e0b135d9cc1c131e8bde76575bb55"

do_install:append:class-nativesdk () {
    rm -f ${D}${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/libnvrtc*.alt.so*
}

FILES:${PN}-dev:remove = "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/*${SOLIBSDEV}"
FILES:${PN} += "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/libnvrtc-builtins.so"
FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/libnvrtc.so"
INSANE_SKIP:${PN} += "dev-so"
BBCLASSEXTEND = "native nativesdk"
