CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

COMPATIBLE_HOST:tegra = "(-)"

MAINSUM = "9d072a6cdb026af6906cbe0b32bf504df1455c9a0a67c592ace0ec2557c9d907"

DEPENDS = "cuda-cupti"
ALLOW_EMPTY:${PN} = "1"
BBCLASSEXTEND = "native nativesdk"
