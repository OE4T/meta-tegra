L4T_DEB_COPYRIGHT_MD5 = "5c7cd503bbe3d6130391b09d9ed1005b"
DEPENDS = "tegra-libraries-core tegra-libraries-nvsci tegra-libraries-cuda"

require tegra-debian-libraries-common.inc

MAINSUM = "8caa940b0bbd81958f523cda7fde8b7c4a91ef41caa9d68c5ab0271ce8553bf2"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libnvpvaintf.so \
"
FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
