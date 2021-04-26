CUDA_PKG = "cuda-nvcc"

require cuda-shared-binaries-${PV}.inc

SRC_URI[main.sha256sum] = "1a0ea57d4c1b1d9394d7e4f6ab94baa2aa49883f4ba2d59a60b750bb88d0fdeb"

do_install_append() {
    for d in bin nvvm nvvmx; do
        rm -rf ${D}${prefix}/local/cuda-${CUDA_VERSION}/$d
    done
}

CONTAINER_CSV_FILES = ""
CONTAINER_CSV_PKGNAME = ""
FILES_${PN} = "${prefix}/local/cuda-${CUDA_VERSION}/include"
FILES_${PN}-dev = ""

BBCLASSEXTEND = "native nativesdk"
