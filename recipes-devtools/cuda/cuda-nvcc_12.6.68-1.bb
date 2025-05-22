CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

DEPENDS = "cuda-cudart cuda-nvvm cuda-crt"

MAINSUM = "89dbfa6e7f791221f9886de6a32871be825d0001f6c4ac5a8c12229fda39c598"
MAINSUM:x86-64 = "f49f81ee3653342d9f7571596c9aef32d5b917ab1fc5e0022c339bf3fb937e0c"

# header files are populated by cuda-nvcc-headers recipes
do_install:append() {
    rm -rf ${D}${prefix}/local/cuda-${CUDA_VERSION}/include
}

FILES:${PN} = "${prefix}/local/cuda-${CUDA_VERSION}"
FILES:${PN}-dev = ""
INSANE_SKIP:${PN} += "dev-so dev-deps"
RDEPENDS:${PN} = "${BPN}-headers"
RDEPENDS:${PN}:append:class-target = " cuda-nvvm-dev cuda-crt-dev"
RDEPENDS:${PN}:append:class-nativesdk = " nativesdk-cuda-environment nativesdk-cuda-nvvm"

BBCLASSEXTEND = "native nativesdk"
