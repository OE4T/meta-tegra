CUDA_PKG = "libcufile libcufile-dev"

require cuda-shared-binaries-11.8.inc

MAINSUM = "f55f9700b64cbae0fc5ea294a7ed64f8d4dacfb314305565f2b9440f35452ce9"
MAINSUM:x86-64 = "837fed2eb6f68d49149549ccb3cd3ab7347ef601a9525c0f9c45f2bd3cb566f3"
DEVSUM = "56769d1d16b8959e42eabc55e32ea9c1ee047a6b16fe4ce1305644d9e2fd8a45"
DEVSUM:x86-64 = "f485f381e84dcdb12e8b3067085a0eacb7ce4f46f388638e32cf8a23d137afbe"

do_install:append() {
    rm -rf ${D}${prefix}/local/cuda-${CUDA_VERSION}/gds
}

BBCLASSEXTEND = "native nativesdk"
