
require cuda-shared-binaries.inc

MAINSUM = "cec8bdb770f98e24be2cad58b00a696b2f115970a09eb51e290fd9e8a4ddb6a2"
MAINSUM:x86-64 = "5be5c79c3fc1cb2d0dad067db70c3d18566298a3f7804932a0b13077a8dc2578"
DEVSUM = "5753ce8d77401146105d5e369bdaa19fa89f84c061eb10e3b6d9c9bfc72860c8"
DEVSUM:x86-64 = "919e94ea84ae4a33535ade98bc5d6e6c6cbcc76e8fa6ba90080dc763a2036ca5"

do_install:append:class-nativesdk () {
    rm -f ${D}${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/libnvrtc*.alt.so*
}

FILES:${PN}-dev:remove = "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/*${SOLIBSDEV}"
FILES:${PN} += "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/libnvrtc-builtins.so"
FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/libnvrtc.so"
INSANE_SKIP:${PN} += "dev-so"
BBCLASSEXTEND = "native nativesdk"
