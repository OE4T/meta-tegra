CUDA_PKG = "cuda-nvvm"
L4T_DEB_GROUP = "cuda-nvcc"

require cuda-shared-binaries-12.9.inc

MAINSUM = "522ddf467f317d242c17f9eeb78cb7b38339b21b6de3a175f49b4d966d55fba2"
MAINSUM:x86-64 = "679b7fa53d30dcb7d2f5f55107274fa502ff18071c6c1235cb941fcbe22a1403"

FILES:${PN} = "${prefix}/local/cuda-${CUDA_VERSION}"
FILES:${PN}-dev = ""
INSANE_SKIP:${PN} += "dev-so"

BBCLASSEXTEND = "native nativesdk"
