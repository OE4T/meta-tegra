CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

MAINSUM = "69e8f2cd14457c24be7572ddd6c9da20aba7a8305b11bd2df54c12f8eba14ca2"
MAINSUM:x86-64 = "ad429690dfdbcc2c5422f37bb1170cb5d5d0a01930e112cf12d76797a02e9aa7"
DEVSUM = "f8dfc87b6bbf43f25897b199f368bd8e324db9beece2b90272a6e47c45c542b8"
DEVSUM:x86-64 = "77eee1bcc4066858abcb5e3498bd6e2322d90b4bc0961eee802a0a4547109af7"

do_compile:prepend() {
	rm -rf ${B}/usr/local/cuda-${CUDA_VERSION}/res
}

FILES:${PN} += "${prefix}/local/cuda-${CUDA_VERSION}/res"

BBCLASSEXTEND = "native nativesdk"
