CUDA_PKG = "libcudla libcudla-dev"

require cuda-shared-binaries-11.8.inc

MAINSUM = "99b23f571c827b2e2688ea52460d0f76c707e4da537e4b17621726128861b9f0"
DEVSUM = "eb5fce86294c6df8c4e363eb758b53fba80b5a77a3089b576ba5bfa207bd7cb6"

RDEPENDS:${PN} = "tegra-libraries-core tegra-libraries-cuda"
