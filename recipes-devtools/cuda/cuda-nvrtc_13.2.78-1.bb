CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

MAINSUM = "f9a56a72c766521e64d659f0f9817e0965d5df6da6227b24ab53846c3a93165f"
MAINSUM:x86-64 = "c237ea3e04e4390d17a0bc047697d3dba2f2db9fe283641ba430afa08ed992b6"
DEVSUM = "d06d64a1d4f7197b4d786b86f190e24a2e1a235bd2ba786b0ac62bb3da60b531"
DEVSUM:x86-64 = "2b4fffd8abceb84ef51e12c2c8d58849c24cb8cef0226bda5a026ee08b84a684"

do_install:append:class-nativesdk () {
    rm -f ${D}${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/libnvrtc*.alt.so*
}

FILES:${PN}-dev:remove = "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/*${SOLIBSDEV}"
FILES:${PN} += "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/libnvrtc-builtins.so"
FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/libnvrtc.so"
INSANE_SKIP:${PN} += "dev-so"
BBCLASSEXTEND = "native nativesdk"
