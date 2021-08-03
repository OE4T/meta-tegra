CUDA_PKG = "${BPN}"

require cuda-shared-binaries-${PV}.inc

MAINSUM = "f8f498108e1e4e3fdc40a1cb80cec44bb3da987b2160a3128d3bcf85a8533bbf"
MAINSUM:x86-64 = "9881098dd47b9d0f4a265904129726f2bb5c6e61041d95e84a276073a4edefe8"

BBCLASSEXTEND = "native nativesdk"
