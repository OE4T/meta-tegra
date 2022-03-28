CUDA_PKG = "${BPN}"

require cuda-shared-binaries.inc

MAINSUM = "ff930d3e0792c467a4f361fe22251987ec6e2c78f7ec5bdbf515c8c4256d8a43"
MAINSUM:x86-64 = "4595b7dd635df08746d0dddb129cd06c2eabc0a9b6303a2e92837bdf67466695"

DEPENDS = "cuda-cupti"
DEPENDS:append:tegra = " tegra-libraries-cuda"
ALLOW_EMPTY:${PN} = "1"
BBCLASSEXTEND = "native nativesdk"
