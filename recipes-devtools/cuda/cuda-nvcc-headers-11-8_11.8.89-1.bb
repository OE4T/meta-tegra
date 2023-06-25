CUDA_PKG = "cuda-nvcc"

require cuda-shared-binaries-11.8.inc

L4T_DEB_GROUP = "cuda-nvcc"
MAINSUM = "55faf663fd702092402bfa26bcf159534473eb625912cee383b0361028d422b6"
MAINSUM:x86-64 = "f72fcab60e94ae2ffbf7c24bedde5329fd7f3ca8fd07b6a5e92fd85759ecabdf"

do_install:append() {
    for d in bin lib nvvm nvvmx; do
        rm -rf ${D}${prefix}/local/cuda-${CUDA_VERSION}/$d
    done
}

FILES:${PN} = "${prefix}/local/cuda-${CUDA_VERSION}/include"
FILES:${PN}-dev = ""

BBCLASSEXTEND = "native nativesdk"
