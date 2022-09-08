L4T_DEB_COPYRIGHT_MD5 = "3d9212d4d5911fa3200298cd55ed6621"
DEPENDS = "tegra-libraries-core tegra-libraries-multimedia alsa-lib"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-multimedia"

require tegra-debian-libraries-common.inc

MAINSUM = "9a1628fef3dce3434b67af1235e0bff595b9cedc2c77a9f735037ce94f286c28"

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
