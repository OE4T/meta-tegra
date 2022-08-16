L4T_DEB_COPYRIGHT_MD5 = "579030142dc986f82ca4bec0a2ae5ba5"
DEPENDS = "tegra-libraries-core tegra-libraries-nvsci tegra-libraries-cuda"

require tegra-debian-libraries-common.inc

MAINSUM = "a73b60c58eaa73ea988fa81d4e686df2384a96bb91762a5a81c2c710726da316"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libnvpvaintf.so \
"
FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
