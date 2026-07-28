CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

DEPENDS = "cuda-cudart libnvvm cuda-crt"

MAINSUM = "f142c71a5dea49fd1f58a97d6899ddf2b80b1cd61da2c5c43eecfb8308121715"
MAINSUM:x86-64 = "5fbb98fe3a1a652bd725104f4099613298cc27c0edc1cd59b20d3c29c756f5ad"

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
