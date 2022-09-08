L4T_DEB_COPYRIGHT_MD5 = "579030142dc986f82ca4bec0a2ae5ba5"
DEPENDS = "tegra-libraries-core tegra-libraries-nvsci tegra-libraries-cuda"

require tegra-debian-libraries-common.inc

MAINSUM = "798303253cc483c6afbd98bf395ea1c20df872ea96e06e084c9bb72fde1eb36a"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libnvpvaintf.so \
"
FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
