CUDA_PKG = "${BPN}"

DEPENDS:tegra = "tegra-libraries-cuda"


require cuda-shared-binaries.inc

MAINSUM = "c0ff7784c64be2896997d1a5a64f774d6f2d13f3a152e496eeb8400a6ce2cd88"
L4T_DEB_GROUP:x86-64 = "nvidia-graphics-drivers"
MAINSUM:x86-64 = "1ee204d44b494efd5941df9b479a2021d42276810d3daf54e09730c3ae7faba9"

FILES:${PN} += "${prefix}/local/cuda-${CUDA_VERSION}/compat"

RDEPENDS:${PN}:class-target = "tegra-libraries-cuda"
INSANE_SKIP:${PN} += "dev-so"

BBCLASSEXTEND = "native nativesdk"
