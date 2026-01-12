CUDA_PKG = "libnvjitlink libnvjitlink-dev"

require cuda-shared-binaries-12.9.inc

MAINSUM = "4e2efd9baeae6b830ace3da3cd96dd9294b6ccfa54e3007b687945e1c4e7bf44"
MAINSUM:x86-64 = "e5db2bbbb1cc571360686ba6ce6898fd15da3db438592777a6472eb7dfe620b4"
DEVSUM = "90f66ab235ba38c572f18ae2ee734e3e801826926a4e62cdcf639c882e298688"
DEVSUM:x86-64 = "68bf8c5fd0c3734ae866e4b5a622db9eb9d3f71fda39d34b496e53384a2304f5"

do_compile:prepend() {
	rm -rf ${B}/usr/local/cuda-${CUDA_VERSION}/res
}

FILES:${PN} += "${prefix}/local/cuda-${CUDA_VERSION}/res"

BBCLASSEXTEND = "native nativesdk"
