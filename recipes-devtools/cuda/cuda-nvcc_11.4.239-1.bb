CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "d30ff7c5fe0ef8c2394f433147cf447b12f3f8cf86f1e888a76429a56e88478a"
MAINSUM:x86-64 = "b7e315fc80a7d95f99dc13652af2e83165f399f919acfc91066272d9e97e7c6f"

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
