CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "1b478656640abed0ffecef36c0dc157f8cbedca1bcd102bf3da7ce8ea7d67c6a"
MAINSUM:x86-64 = "c849dc1cf1a00ead6b53a685348d8a644d365205afd968e1b7f6ef3e6c0db71a"

BBCLASSEXTEND = "native nativesdk"
