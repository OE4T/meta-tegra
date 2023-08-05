L4T_DEB_COPYRIGHT_MD5 = "579030142dc986f82ca4bec0a2ae5ba5"
DEPENDS = "tegra-libraries-core tegra-libraries-nvsci tegra-libraries-cuda"

require tegra-debian-libraries-common.inc

MAINSUM = "e21eba1d8980ee5f955551574cae562149dcadc71f759644bd53b868c202dea8"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libnvpvaintf.so \
"
FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
