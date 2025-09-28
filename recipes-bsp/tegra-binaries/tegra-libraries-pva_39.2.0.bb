L4T_DEB_COPYRIGHT_MD5 = "9bc6d8162be2293e5c57f3798c73bb44"
DEPENDS = "tegra-libraries-core tegra-libraries-nvsci tegra-libraries-cuda"

require tegra-debian-libraries-common.inc

MAINSUM = "be624f91de5506cc9b975068ec0e8e48540a8839f422745c436d3f09ab5509b3"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libnvpvaintf.so \
    nvidia/libnvpvaumd_cuda.so \
    nvidia/libnvpvaumd_core.so \
"
FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
