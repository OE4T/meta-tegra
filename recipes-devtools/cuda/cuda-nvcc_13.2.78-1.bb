CUDA_PKG = "${BPN}"
CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

DEPENDS = "cuda-cudart libnvvm cuda-crt"

MAINSUM = "3b943b0c907d6c56aa8924d9aabd124813c8a329f7f3a836198e0757fc835a15"
MAINSUM:x86-64 = "b847731f2e4ab29858f7490d7b0d1af33fc119c8191f07f44181f204fbc14d96"

# header files are populated by cuda-nvcc-headers recipes
do_install:append() {
    rm -rf ${D}${prefix}/local/cuda-${CUDA_VERSION}/include
}

FILES:${PN} = "${prefix}/local/cuda-${CUDA_VERSION}"
FILES:${PN}-dev = ""
INSANE_SKIP:${PN} += "dev-so dev-deps"
RDEPENDS:${PN} = "${BPN}-headers"
RDEPENDS:${PN}:append:class-target = " libnvvm-dev cuda-crt-dev"
RDEPENDS:${PN}:append:class-nativesdk = " nativesdk-cuda-environment nativesdk-libnvvm"

BBCLASSEXTEND = "native nativesdk"
