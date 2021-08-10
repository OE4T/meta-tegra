CUDA_PKG = "cuda-nvcc"

require cuda-shared-binaries-${PV}.inc

L4T_DEB_GROUP = "cuda-nvcc"
MAINSUM = "86c138d6903fa35bb512414bbb98dfd519d3e5ee743dc846e9fb1aa42f7e0391"
MAINSUM:x86-64 = "573d0fed9398a024ba0a0f6aeeb796a13535b227de6b95de22673ab76853df55"

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
