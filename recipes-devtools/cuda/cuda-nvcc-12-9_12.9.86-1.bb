CUDA_PKG = "cuda-nvcc"

require cuda-shared-binaries-12.9.inc

DEPENDS = "cuda-cudart-12-9 cuda-nvvm-12-9 cuda-crt-12-9"

MAINSUM = "337b9720ba0d71a3da4502a0f82e0f132a5edc7629b04275a191efb131c60617"
MAINSUM:x86-64 = "66c11ad4fba8c12c44a1fec346c5125011e2ed519b98a787966dda0df9043b45"

# header files are populated by cuda-nvcc-headers-12-9 recipes
do_install:append() {
    rm -rf ${D}${prefix}/local/cuda-${CUDA_VERSION}/include
}

FILES:${PN} = "${prefix}/local/cuda-${CUDA_VERSION}"
FILES:${PN}-dev = ""
INSANE_SKIP:${PN} += "dev-so dev-deps"
RDEPENDS:${PN} = "cuda-nvcc-headers-12-9"
RDEPENDS:${PN}:append:class-target = " cuda-nvvm-12-9-dev cuda-crt-12-9-dev"
RDEPENDS:${PN}:append:class-nativesdk = " nativesdk-cuda-environment nativesdk-cuda-nvvm-12-9"

BBCLASSEXTEND = "native nativesdk"
