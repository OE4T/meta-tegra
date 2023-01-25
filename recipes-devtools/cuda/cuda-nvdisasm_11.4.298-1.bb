CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "a7eb2f26db67c3823f9cb02db46d9728e907dca96dcb260a62448f064ac491d1"
MAINSUM:x86-64 = "b535748064852dd9dbff0f70ce70bf2046f869be97e6e818be451a04b1cb8be5"

BBCLASSEXTEND = "native nativesdk"
