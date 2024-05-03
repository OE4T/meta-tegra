CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

COMPATIBLE_HOST:tegra = "(-)"

MAINSUM = "ee987df9aa0fef7d9b2b162d29acf0e81d3df888528222e473f230cd7ecdd61a"

DEPENDS = "cuda-cupti"
ALLOW_EMPTY:${PN} = "1"
BBCLASSEXTEND = "native nativesdk"
