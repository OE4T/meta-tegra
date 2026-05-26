CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

MAINSUM = "69e8f2cd14457c24be7572ddd6c9da20aba7a8305b11bd2df54c12f8eba14ca2"
MAINSUM:x86-64 = "d9e00efe8103cb4cf7a303f07e32aec88bca5700eb12045b551099577dc2afa2"
DEVSUM = "f8dfc87b6bbf43f25897b199f368bd8e324db9beece2b90272a6e47c45c542b8"
DEVSUM:x86-64 = "22a60e0f33bf6657072fac6e74433e0631b2f32ff32436ead245ba9ca36ccaa3"

do_compile:prepend() {
	rm -rf ${B}/usr/local/cuda-${CUDA_VERSION}/res
}

FILES:${PN} += "${prefix}/local/cuda-${CUDA_VERSION}/res"

BBCLASSEXTEND = "native nativesdk"
