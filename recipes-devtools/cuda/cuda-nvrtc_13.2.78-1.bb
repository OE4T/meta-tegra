CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

MAINSUM = "f9a56a72c766521e64d659f0f9817e0965d5df6da6227b24ab53846c3a93165f"
MAINSUM:x86-64 = "2c3e1633f35d9cc0f6154d585c11bf4ca8be6d0d65fad74888f40c229ca0a17f"
DEVSUM = "d06d64a1d4f7197b4d786b86f190e24a2e1a235bd2ba786b0ac62bb3da60b531"
DEVSUM:x86-64 = "f2c4e334d973fa3aaa6e0c26c97c1bd020bbc961a30cef7d4e70e461e25c140a"

do_install:append:class-nativesdk () {
    rm -f ${D}${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/libnvrtc*.alt.so*
}

FILES:${PN}-dev:remove = "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/*${SOLIBSDEV}"
FILES:${PN} += "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/libnvrtc-builtins.so"
FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/libnvrtc.so"
INSANE_SKIP:${PN} += "dev-so"
BBCLASSEXTEND = "native nativesdk"
