L4T_DEB_COPYRIGHT_MD5 = "8c84e973feeab684f7575379648f700c"
DEPENDS = "tegra-libraries-core tegra-libraries-multimedia alsa-lib"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-multimedia"

require tegra-debian-libraries-common.inc

MAINSUM = "2ae5ffaef3bebb51e2f95140a54b553e5535ed31b8fb6b2a7516a5918c80d674"

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
