CUDA_PKG = "cuda-nvcc"

require cuda-shared-binaries-11.8.inc

MAINSUM = "55faf663fd702092402bfa26bcf159534473eb625912cee383b0361028d422b6"
MAINSUM:x86-64 = "f72fcab60e94ae2ffbf7c24bedde5329fd7f3ca8fd07b6a5e92fd85759ecabdf"

# header files are populated by cuda-nvcc-headers-11-8 recipes
do_install:append() {
    rm -rf ${D}${prefix}/local/cuda-${CUDA_VERSION}/include
}

FILES:${PN} = "${prefix}/local/cuda-${CUDA_VERSION}"
FILES:${PN}-dev = ""
INSANE_SKIP:${PN} += "dev-so"
RDEPENDS:${PN} = "cuda-nvcc-headers-11-8"
RDEPENDS:${PN}:append:class-nativesdk = " nativesdk-cuda-environment"

BBCLASSEXTEND = "native nativesdk"
