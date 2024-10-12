CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

COMPATIBLE_HOST:tegra = "(-)"

MAINSUM = "6b0171bb815ff6d62cb84f51868afa12975d71f2ba60d43be0dd6a29a7a989d4"

DEPENDS = "cuda-cupti"
ALLOW_EMPTY:${PN} = "1"
BBCLASSEXTEND = "native nativesdk"
