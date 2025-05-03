L4T_DEB_COPYRIGHT_MD5 = "5c7cd503bbe3d6130391b09d9ed1005b"
DEPENDS = "tegra-libraries-core tegra-libraries-nvsci tegra-libraries-cuda"

require tegra-debian-libraries-common.inc

MAINSUM = "e56c579a47a67867af78c2c2146c59eaa2c4e63146ebdf54273db60ca9921534"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libnvpvaintf.so \
"
FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
