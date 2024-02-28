L4T_DEB_COPYRIGHT_MD5 = "8795129d4eaddc7f1ad4555999cf6520"
DEPENDS = "tegra-libraries-core tegra-libraries-nvsci tegra-libraries-cuda"

require tegra-debian-libraries-common.inc

MAINSUM = "371b956555d8869c2aba3f880cd1111378a347efea61896df0a1e0d4df8dcd90"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libnvpvaintf.so \
"
FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
