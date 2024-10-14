L4T_DEB_COPYRIGHT_MD5 = "5c7cd503bbe3d6130391b09d9ed1005b"
DEPENDS = "tegra-libraries-core tegra-libraries-nvsci tegra-libraries-cuda"

require tegra-debian-libraries-common.inc

MAINSUM = "3b121f9cc6772624b40b3fc5cff8286afca98e305b351f95d7da8037d52d9a92"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libnvpvaintf.so \
"
FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
