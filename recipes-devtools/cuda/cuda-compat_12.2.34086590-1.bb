CUDA_PKG = "${BPN}"

DEPENDS:tegra = "tegra-libraries-cuda"

require cuda-shared-binaries.inc

MAINSUM = "f4ac00c3c0cf7a5cca82407b121fd738914f1e763f5cb171c647142009be7a2f"

FILES:${PN} += "${prefix}/local/cuda-${CUDA_VERSION}/compat"

RDEPENDS:${PN} = "tegra-libraries-cuda"
INSANE_SKIP:${PN} += "dev-so"
