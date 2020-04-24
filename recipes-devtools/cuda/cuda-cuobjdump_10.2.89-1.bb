CUDA_PKG = "${BPN}"

require cuda-shared-binaries-${PV}.inc

SRC_URI[main.sha256sum] = "719b32f039cd8ed6123e1f9e3fa9badcf4c6ba5ac4a0b24a97d2db88e0764e1e"

BBCLASSEXTEND = "native nativesdk"
