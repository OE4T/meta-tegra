L4T_DEB_COPYRIGHT_MD5 = "579030142dc986f82ca4bec0a2ae5ba5"
DEPENDS = "tegra-libraries-core tegra-libraries-nvsci tegra-libraries-cuda"

require tegra-debian-libraries-common.inc

MAINSUM = "76612276f4a85140dab03880dea4c682f1639d95a8814dfaccb635af972f155d"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libnvpvaintf.so \
"
FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
