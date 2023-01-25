require cuda-shared-binaries.inc

MAINSUM = "f0e4150e89624d85dc6aaaec09fad97e36d285212f6765b24db9e69b85c2f0b0"
MAINSUM:x86-64 = "a4220f40dd1dffa24a65ace2dcafa4e21d3244e33ae2ec741d328279e8aa5931"
DEVSUM = "204caec1733d39008af5840e22c4e620aff5ee44ecb329fab5b57d6eeea9e06a"
DEVSUM:x86-64 = "df6e6bca6bb44e3bbb1980a86406e22c432ad0f1aedbd0a5f1e80efb475fab63"

FILES:${PN}-dev:remove = "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/*${SOLIBSDEV}"
FILES:${PN} += "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/libnvrtc-builtins.so"
FILES:${PN}-dev += "${prefix}/local/cuda-${CUDA_VERSION}/${baselib}/libnvrtc.so"
INSANE_SKIP:${PN} += "dev-so"
BBCLASSEXTEND = "native nativesdk"
