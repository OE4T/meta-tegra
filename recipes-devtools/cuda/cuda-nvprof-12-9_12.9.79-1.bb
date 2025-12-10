CUDA_PKG = "cuda-nvprof"

require cuda-shared-binaries-12.9.inc

COMPATIBLE_HOST:tegra = "(-)"

MAINSUM = "1ae8ec482368cbe2540c6b234375a3b957fe9a1301a259f482f207c98abeff1c"

DEPENDS = "cuda-cupti-12-9"
ALLOW_EMPTY:${PN} = "1"
BBCLASSEXTEND = "native nativesdk"
