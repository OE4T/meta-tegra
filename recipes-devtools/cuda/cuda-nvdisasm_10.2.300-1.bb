CUDA_PKG = "${BPN}"

require cuda-shared-binaries-${PV}.inc

MAINSUM = "6b9a6f123bc46d525abf351e8720fa6da5573fd9caeebe312590105bef71a146"
MAINSUM:x86-64 = "929a041d8a2919fcdf1a65af72fa6fd1d510734a471890b0084407e93785f720"

BBCLASSEXTEND = "native nativesdk"
