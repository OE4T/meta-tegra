CUDA_PKG = "${BPN}"

require cuda-shared-binaries-${PV}.inc

SRC_URI[main.sha256sum] = "b4f9dedf0c21dbc75daadbabfde6aa17a73b6bdad0d5a18fae3da6e9717bfc99"

BBCLASSEXTEND = "native nativesdk"
