CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "973580bee9d6bf01ccfe1d2bc8a03437384c0fbf68af785390aa513062dc34b7"
MAINSUM:x86-64 = "ba8f11b3e0f589686dcfe8a6fee9ddf3eeb61dd58cde74e218ed730054a2f3bf"

BBCLASSEXTEND = "native nativesdk"
