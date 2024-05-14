CUDA_PKG = "${BPN}"
L4T_DEB_GROUP = "cuda-nvcc"

require cuda-shared-binaries.inc

MAINSUM = "1ce8fac08a532f9202f1395a4b2c7be78cf9976663373cf563d79e7164b3b158"
MAINSUM:x86-64 = "7cd46649dbad60b59860f0965d92eea87f783abfe2cbd2c9b49a0c8544161e3a"

FILES:${PN} = "${prefix}/local/cuda-${CUDA_VERSION}"
FILES:${PN}-dev = ""
INSANE_SKIP:${PN} += "dev-so"

BBCLASSEXTEND = "native nativesdk"
