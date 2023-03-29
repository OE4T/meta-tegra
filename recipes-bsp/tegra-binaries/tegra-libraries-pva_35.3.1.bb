L4T_DEB_COPYRIGHT_MD5 = "579030142dc986f82ca4bec0a2ae5ba5"
DEPENDS = "tegra-libraries-core tegra-libraries-nvsci tegra-libraries-cuda"

require tegra-debian-libraries-common.inc

MAINSUM = "a99f85d15d79b45b43f98879b3bf39211925f6b578209f8e258d73318ab4c16a"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libnvpvaintf.so \
"
FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
