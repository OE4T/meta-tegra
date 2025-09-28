CUDA_PKG = "${BPN}"
CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

DEPENDS = "cuda-cudart libnvvm cuda-crt"

MAINSUM = "c320d58169ba86a3adbbd48955efa9d2a62c08009f2bbeca8721317533bc70cc"
MAINSUM:x86-64 = "8b7ebed1b37bf9eda238a3a3fe959812aa79c1c6b2498cb21df144890e01efd6"

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
