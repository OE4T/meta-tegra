L4T_DEB_COPYRIGHT_MD5 = "8795129d4eaddc7f1ad4555999cf6520"
DEPENDS = "tegra-libraries-core tegra-libraries-nvsci tegra-libraries-cuda"

require tegra-debian-libraries-common.inc

MAINSUM = "e21eba1d8980ee5f955551574cae562149dcadc71f759644bd53b868c202dea8"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libnvpvaintf.so \
"
FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
