CUDA_PKG = "${BPN}"

require cuda-shared-binaries-${PV}.inc

SRC_URI[main.sha256sum] = "1e04820f53fb96b737c6b5d92ae2c1c54414e84a87ae9a55a00fc78c05e4e33f"

BBCLASSEXTEND = "native nativesdk"
