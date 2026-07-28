L4T_DEB_COPYRIGHT_MD5 = "8c84e973feeab684f7575379648f700c"
DEPENDS = "tegra-libraries-core tegra-libraries-multimedia alsa-lib"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-multimedia"

require tegra-debian-libraries-common.inc

MAINSUM = "4314c6f44b8dbe707dc2634030d2153d8f07efabe520483f6d00309f524a7262"

inherit features_check

REQUIRED_DISTRO_FEATURES = "opengl"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libnvomx.so \
    nvidia/libnvomxilclient.so \
"

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
