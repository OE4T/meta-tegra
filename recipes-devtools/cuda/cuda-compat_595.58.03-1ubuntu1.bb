CUDA_PKG = "${BPN}"

DEPENDS:tegra = "tegra-libraries-cuda"


require cuda-shared-binaries.inc

MAINSUM = "139a8f34e54339244cc98f270e7a92c2b12bea81d19c55899a9c9dcf1e0cc3d7"
L4T_DEB_GROUP:x86-64 = "nvidia-graphics-drivers"
MAINSUM:x86-64 = "ddc40de2430fa34924d1b6ce6fc0fabe5c0d6bad2a0615de90fb8e9dbf5d40d5"

FILES:${PN} += "${prefix}/local/cuda-${CUDA_VERSION}/compat"

RDEPENDS:${PN}:class-target = "tegra-libraries-cuda"
INSANE_SKIP:${PN} += "dev-so"

BBCLASSEXTEND = "native nativesdk"
