CUDA_PKG = "${BPN}"
L4T_DEB_GROUP = "cuda-nvcc"

require cuda-shared-binaries.inc

MAINSUM = "32ba801d719a593cb35abd4aeb93c672c0a02d47539455b5780c6004f03462cd"
MAINSUM:x86-64 = "dd229d8e29d5c6ab5d55c7de31efbcf9d89f490cff600555c0d33eb6c106e57b"

FILES:${PN} = "${prefix}/local/cuda-${CUDA_VERSION}"
FILES:${PN}-dev = ""
INSANE_SKIP:${PN} += "dev-so"

BBCLASSEXTEND = "native nativesdk"
