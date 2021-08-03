CUDA_PKG = "${BPN}"

require cuda-shared-binaries-${PV}.inc

MAINSUM = "9523033692cca5a2b29cd69942d69036deedacb8eb3273395662f6024b2c27f9"
MAINSUM:x86-64 = "b2fc9a5cdf02dec9df7bd91ef9815e1b50e500afc130d537cd1c72f54441dd44"

BBCLASSEXTEND = "native nativesdk"
