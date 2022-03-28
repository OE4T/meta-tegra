CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "9da644cf9ea45f918ab485e5c5dfac76bee2928d7ccb14f40a14d035193c98b2"
MAINSUM:x86-64 = "2ecf7e58481c7f55374c1b7eb17022519ab1fe12a1df85ce9fc13c78d1e06d59"

# header files are populated by cuda-nvcc-headers recipes
do_install:append() {
    rm -rf ${D}${prefix}/local/cuda-${CUDA_VERSION}/include
}

FILES:${PN} = "${prefix}/local/cuda-${CUDA_VERSION}"
FILES:${PN}-dev = ""
INSANE_SKIP:${PN} += "dev-so"
RDEPENDS:${PN} = "${BPN}-headers"
RDEPENDS:${PN}:append:class-nativesdk = " nativesdk-cuda-environment"

BBCLASSEXTEND = "native nativesdk"
