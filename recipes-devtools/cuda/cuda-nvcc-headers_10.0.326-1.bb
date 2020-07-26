CUDA_PKG = "cuda-nvcc"

require cuda-shared-binaries-${PV}.inc

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
