L4T_DEB_COPYRIGHT_MD5 = "3d9212d4d5911fa3200298cd55ed6621"
DEPENDS = "tegra-libraries-core tegra-libraries-multimedia alsa-lib"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-multimedia"

require tegra-debian-libraries-common.inc

<<<<<<<< HEAD:recipes-bsp/tegra-binaries/tegra-libraries-omx_32.7.2.bb
MAINSUM = "615f144019af8f083e46ac7db005c087d953856795fc26fc528f323bc52763bb"
MAINSUM:tegra210 = "b6e211042ae57390cea2add1aad52903b31fbc950ac9b6613c4b30e5974092e6"
========
MAINSUM = "8df6cf44eecbe35690163b1bbb65cc4cf4160494a847ef01f1f2ba8702ebc392"
>>>>>>>> 390a51f3 (tegra-binaries: update for 34.1.0):recipes-bsp/tegra-binaries/tegra-libraries-omx_34.1.0.bb

inherit features_check

REQUIRED_DISTRO_FEATURES = "opengl"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libnvomx.so \
    tegra/libnvomxilclient.so \
"

# XXX---
# Temporary for use with binary-only NVIDIA OpenMAX gstreamer plugin
do_install:append() {
    install -d ${D}/usr/lib/aarch64-linux-gnu/tegra
    ln -sf ${libdir}/libnvomx.so ${D}/usr/lib/aarch64-linux-gnu/tegra/libnvomx.so
}
FILES:${PN} += "/usr/lib/aarch64-linux-gnu/tegra"
# ---XXX

FILES_SOLIBSDEV = ""
SOLIBS = ".so*"
