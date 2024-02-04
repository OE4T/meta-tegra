L4T_DEB_COPYRIGHT_MD5 = "8795129d4eaddc7f1ad4555999cf6520"
DEPENDS = "tegra-libraries-core tegra-libraries-nvsci tegra-libraries-cuda"

require tegra-debian-libraries-common.inc

MAINSUM = "719ee627276dc21f8117fee1cca4c65bcc04e7d85e91afb7bcf639e549646648"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libnvpvaintf.so \
"
FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
