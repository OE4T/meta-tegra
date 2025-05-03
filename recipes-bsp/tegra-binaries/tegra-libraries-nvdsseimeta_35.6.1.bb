DESCRIPTION = "NVIDIA SEI metadata support library for GStreamer"
L4T_DEB_COPYRIGHT_MD5 = "20bbd485b9b57fbc0b55d6efc08e3f4a"

DEPENDS = "glib-2.0 gstreamer1.0"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-gstreamer"

require tegra-debian-libraries-common.inc

MAINSUM = "930c1ec408a160f17de8fe7932883197efaeed714d1575fda2a3b789337f0e06"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libgstnvdsseimeta.so.1.0.0 \
"
do_install() {
    install_libraries
    ln -s libgstnvdsseimeta.so.1.0.0 ${D}${libdir}/libgstnvdsseimeta.so
}
