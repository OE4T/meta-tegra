CUDA_PKG = "cuda-nvcc"

require cuda-shared-binaries-${PV}.inc

MAINSUM = "1a0ea57d4c1b1d9394d7e4f6ab94baa2aa49883f4ba2d59a60b750bb88d0fdeb"
MAINSUM:x86-64 = "a4d649cb1433f7f9e2453abf2caf606819db0eb57def025568f276c31da53f85"

do_install:append() {
    for d in bin nvvm nvvmx; do
        rm -rf ${D}${prefix}/local/cuda-${CUDA_VERSION}/$d
    done
}

CONTAINER_CSV_FILES = ""
CONTAINER_CSV_PKGNAME = ""
FILES:${PN} = "${prefix}/local/cuda-${CUDA_VERSION}/include"
FILES:${PN}-dev = ""

BBCLASSEXTEND = "native nativesdk"
