L4T_DEB_COPYRIGHT_MD5 = "3d9212d4d5911fa3200298cd55ed6621"
DEPENDS = "tegra-libraries-core tegra-libraries-multimedia alsa-lib"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-multimedia"

require tegra-debian-libraries-common.inc

MAINSUM = "563784b9084fb4cecf36021bb8b6a4da014d27c8d4605ed830bd0fdcbbe16f09"
MAINSUM:tegra210 = "3c897968e5874d48d9abc2beb99c02f75863f3a21ab0b17eaaa0d574a2e9890a"

inherit features_check

REQUIRED_DISTRO_FEATURES = "opengl"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libnvomx.so \
    tegra/libnvomxilclient.so \
"

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
