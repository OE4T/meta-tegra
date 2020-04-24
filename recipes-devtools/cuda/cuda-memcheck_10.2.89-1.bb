CUDA_PKG = "${BPN}"

require cuda-shared-binaries-${PV}.inc

SRC_URI[main.sha256sum] = "f8f498108e1e4e3fdc40a1cb80cec44bb3da987b2160a3128d3bcf85a8533bbf"

BBCLASSEXTEND = "native nativesdk"
