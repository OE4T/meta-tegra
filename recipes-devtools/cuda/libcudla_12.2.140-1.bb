require cuda-shared-binaries.inc

MAINSUM = "65eede2a075090411ec6f22c2315fdfcafe91865410f771be514048d4bf269e2"
DEVSUM = "463b620f20ec76cc458163cb7c9cfd54c7d13130c6467a29f7b0ea5c35bca0ed"

RDEPENDS:${PN} = "tegra-libraries-core tegra-libraries-cuda cuda-compat"
