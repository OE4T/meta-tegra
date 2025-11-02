L4T_DEB_COPYRIGHT_MD5 = "9bc6d8162be2293e5c57f3798c73bb44"
DEPENDS = "tegra-libraries-core tegra-libraries-nvsci tegra-libraries-cuda"

require tegra-debian-libraries-common.inc

MAINSUM = "b6e0af6508ebe11262c36a668b8481832c2d73f09ece29b6dbdb9488fc37934d"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libnvpvaintf.so \
    nvidia/libnvpvaumd_cuda.so \
    nvidia/libnvpvaumd_core.so \
"
FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
