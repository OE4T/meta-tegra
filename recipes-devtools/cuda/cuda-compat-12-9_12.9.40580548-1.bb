CUDA_PKG = "cuda-compat"

DEPENDS:tegra = "tegra-libraries-cuda"

require cuda-shared-binaries-12.9.inc

MAINSUM = "36f3cd8f5b89b2c539a5e13e0eb5a3e5d860b9e2fe567a2ba0651b0e477e127c"

FILES:${PN} += "${prefix}/local/cuda-${CUDA_VERSION}/compat"

RDEPENDS:${PN} = "tegra-libraries-cuda"
INSANE_SKIP:${PN} += "dev-so"
