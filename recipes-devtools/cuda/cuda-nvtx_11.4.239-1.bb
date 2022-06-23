CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

COMPATIBLE_HOST:x86-64 = "(-)"
MAINSUM = "b8669c8e819053d7c78c13c374b86871b131cc233d7c240d1b4c4d21c0369f6f"

BBCLASSEXTEND = "native nativesdk"
