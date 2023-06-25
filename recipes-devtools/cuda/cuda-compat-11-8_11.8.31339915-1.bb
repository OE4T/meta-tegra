CUDA_PKG = "cuda-compat"

DEPENDS:tegra = "tegra-libraries-core"

require cuda-shared-binaries-11.8.inc

MAINSUM = "3014fc6796b2d693c09385c8561b87c4850294b59b5cff986f7813f2f3921cfa"

FILES:${PN} += "${prefix}/local/cuda-${CUDA_VERSION}/compat"

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
INSANE_SKIP:${PN} += "dev-so"
