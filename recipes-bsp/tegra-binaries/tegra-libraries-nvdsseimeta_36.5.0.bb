DESCRIPTION = "NVIDIA SEI metadata support library for GStreamer"
L4T_DEB_COPYRIGHT_MD5 = "cf5e527faa5ef67c91ec75b7f6c501e7"

DEPENDS = "glib-2.0 gstreamer1.0"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-gstreamer"
L4T_DEB_IS_COMMON = "1"

require tegra-debian-libraries-common.inc

MAINSUM = "188189e005aeb9445aa33134573056cf82b203a989c33e75fa6798fb8265e3e8"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libgstnvdsseimeta.so.1.0.0 \
"
do_install() {
    install_libraries
    ln -s libgstnvdsseimeta.so.1.0.0 ${D}${libdir}/libgstnvdsseimeta.so
}
