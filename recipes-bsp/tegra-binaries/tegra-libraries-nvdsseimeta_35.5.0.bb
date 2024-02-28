DESCRIPTION = "NVIDIA SEI metadata support library for GStreamer"
L4T_DEB_COPYRIGHT_MD5 = "d16b7bdb9e0290d94df6986b72214fb8"

DEPENDS = "glib-2.0 gstreamer1.0"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-gstreamer"

require tegra-debian-libraries-common.inc

MAINSUM = "1bca7270934650a3884b934c4bdb5695730a6de8e10cf961968012af13eccb03"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libgstnvdsseimeta.so.1.0.0 \
"
do_install() {
    install_libraries
    ln -s libgstnvdsseimeta.so.1.0.0 ${D}${libdir}/libgstnvdsseimeta.so
}
