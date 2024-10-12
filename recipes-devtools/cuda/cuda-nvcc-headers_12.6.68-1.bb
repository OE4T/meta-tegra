CUDA_PKG = "cuda-nvcc"

require cuda-shared-binaries.inc

L4T_DEB_GROUP = "cuda-nvcc"
MAINSUM = "89dbfa6e7f791221f9886de6a32871be825d0001f6c4ac5a8c12229fda39c598"
MAINSUM:x86-64 = "f49f81ee3653342d9f7571596c9aef32d5b917ab1fc5e0022c339bf3fb937e0c"

do_install:append() {
    for d in bin lib nvvm nvvmx; do
        rm -rf ${D}${prefix}/local/cuda-${CUDA_VERSION}/$d
    done
}

FILES:${PN} = "${prefix}/local/cuda-${CUDA_VERSION}/include"
FILES:${PN}-dev = ""

BBCLASSEXTEND = "native nativesdk"
