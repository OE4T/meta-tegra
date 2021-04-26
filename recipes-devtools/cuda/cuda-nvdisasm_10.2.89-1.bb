CUDA_PKG = "${BPN}"

require cuda-shared-binaries-${PV}.inc

SRC_URI[main.sha256sum] = "9523033692cca5a2b29cd69942d69036deedacb8eb3273395662f6024b2c27f9"

BBCLASSEXTEND = "native nativesdk"
