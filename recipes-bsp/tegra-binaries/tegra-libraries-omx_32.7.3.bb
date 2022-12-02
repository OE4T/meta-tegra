L4T_DEB_COPYRIGHT_MD5 = "3d9212d4d5911fa3200298cd55ed6621"
DEPENDS = "tegra-libraries-core tegra-libraries-multimedia alsa-lib"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-multimedia"

require tegra-debian-libraries-common.inc

MAINSUM = "438bf54dd274c35696bee1f3b61c04dbd881fca22747e08a0b72f8cf7266c94d"
MAINSUM:tegra210 = "d76bcf6a3b6f92781562ac8b3fb113d0243484bda890a4cd1247ad718bf14a2e"

inherit features_check

REQUIRED_DISTRO_FEATURES = "opengl"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libnvomx.so \
    tegra/libnvomxilclient.so \
"

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
