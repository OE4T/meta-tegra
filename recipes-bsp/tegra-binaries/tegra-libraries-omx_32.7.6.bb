L4T_DEB_COPYRIGHT_MD5 = "da66dd592b6aab6a884628599ea927fe"
DEPENDS = "tegra-libraries-core tegra-libraries-multimedia alsa-lib"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-multimedia"

require tegra-debian-libraries-common.inc

MAINSUM = "6589ce63514866c94db9fc76c0baa395397aa51cef6b191435a4520a97704cc2"
MAINSUM:tegra210 = "e85b22ac1988e7e8926258efd7a54b16fa4fcdbb15810b73dc817570524f0d7b"

inherit features_check

REQUIRED_DISTRO_FEATURES = "opengl"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libnvomx.so \
    tegra/libnvomxilclient.so \
"

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
