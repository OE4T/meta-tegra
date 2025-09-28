CUDA_INSTALL_ARCH:class-target = "sbsa"
CUDA_INSTALL_ARCH:class-native = "${HOST_ARCH}"

require cuda-shared-binaries.inc

MAINSUM = "18046afb2e56ae3392038bcca6263a8ac69465a40b5a9cda370a06c8be6848c5"
MAINSUM:x86-64 = "c69ae30fcf71b7e764e4fdce4e6a834554a6183756ff441da066aa5a1e50a8d6"
DEVSUM = "b78393abda0f21f28ec766e10d7b6181fd6d49e5bd27fcecf813d14a14fafe51"
DEVSUM:x86-64 = "9c39202a7cf29860395d46ad13994e2cba5c0b40dc492ae8d91f865f92a5f406"

do_compile:prepend() {
	rm -rf ${B}/usr/local/cuda-${CUDA_VERSION}/res
}

FILES:${PN} += "${prefix}/local/cuda-${CUDA_VERSION}/res"

BBCLASSEXTEND = "native nativesdk"
