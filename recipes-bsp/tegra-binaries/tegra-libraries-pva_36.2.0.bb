L4T_DEB_COPYRIGHT_MD5 = "8795129d4eaddc7f1ad4555999cf6520"
DEPENDS = "tegra-libraries-core tegra-libraries-nvsci tegra-libraries-cuda"

require tegra-debian-libraries-common.inc

MAINSUM = "0d9d4af4be0436b1c0ac68dc0e7fa1c73eeb0d27338f9345a7dd70e028f15b85"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libnvpvaintf.so \
"
FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
