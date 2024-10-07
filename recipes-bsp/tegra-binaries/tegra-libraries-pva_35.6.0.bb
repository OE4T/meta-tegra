L4T_DEB_COPYRIGHT_MD5 = "5c7cd503bbe3d6130391b09d9ed1005b"
DEPENDS = "tegra-libraries-core tegra-libraries-nvsci tegra-libraries-cuda"

require tegra-debian-libraries-common.inc

MAINSUM = "971db161da65a3a40bc2541322c643038e618f04819e032abfa38be33616fd5f"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libnvpvaintf.so \
"
FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
