CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "c977505a308ee8090fdfa98824d55bafeb3803d1c9410bd44cbda9ac124a145b"
MAINSUM:x86-64 = "6e4d01d696a367c04c03fdfdd8afd2909a68f7519cdff1ae383990356daa8081"
BBCLASSEXTEND = "native nativesdk"
