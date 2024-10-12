CUDA_PKG = "${BPN}"

DEPENDS:tegra = "tegra-libraries-cuda"

require cuda-shared-binaries.inc

MAINSUM = "a5bc64122f78abd2e87cf4282803e40ccef3aae4362d1b8ae87914e2970adc4b"

FILES:${PN} += "${prefix}/local/cuda-${CUDA_VERSION}/compat"

RDEPENDS:${PN} = "tegra-libraries-cuda"
INSANE_SKIP:${PN} += "dev-so"
