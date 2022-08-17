CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

COMPATIBLE_HOST:tegra = "(-)"

MAINSUM = "5b53c9f184d69db86915367fbce5ebf9c7462c9323b3eb8ebf7439589760b9b6"

DEPENDS = "cuda-cupti"
ALLOW_EMPTY:${PN} = "1"
BBCLASSEXTEND = "native nativesdk"
