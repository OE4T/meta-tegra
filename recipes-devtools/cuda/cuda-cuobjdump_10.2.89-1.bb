CUDA_PKG = "${BPN}"

require cuda-shared-binaries-${PV}.inc

MAINSUM = "719b32f039cd8ed6123e1f9e3fa9badcf4c6ba5ac4a0b24a97d2db88e0764e1e"
MAINSUM:x86-64 = "3b61bf94e4f2eb1bc250848badcae753b13aeae7b4e0482fd42ed47e928b9fa3"
BBCLASSEXTEND = "native nativesdk"
