L4T_DEB_COPYRIGHT_MD5 = "8c84e973feeab684f7575379648f700c"
DEPENDS = "tegra-libraries-core tegra-libraries-multimedia alsa-lib"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-multimedia"

require tegra-debian-libraries-common.inc

MAINSUM = "cf2222be0fa337c2d0404ced0418befe87d5aace999c0bad175fb339f1ac4d48"

inherit features_check

REQUIRED_DISTRO_FEATURES = "opengl"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libnvomx.so \
    nvidia/libnvomxilclient.so \
"

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
