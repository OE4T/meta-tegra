L4T_DEB_COPYRIGHT_MD5 = "da66dd592b6aab6a884628599ea927fe"
DEPENDS = "tegra-libraries-core tegra-libraries-multimedia alsa-lib"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-multimedia"

require tegra-debian-libraries-common.inc

MAINSUM = "00aee7780face8a801d1bd612eaef2beaa01b72738acdbc3a83a3dd050830ced"
MAINSUM:tegra210 = "faf67ea3283106cbe825078e6d22c5d75ebae2a28d5c480071ffcd000ca8042e"

inherit features_check

REQUIRED_DISTRO_FEATURES = "opengl"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libnvomx.so \
    tegra/libnvomxilclient.so \
"

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
