DESCRIPTION = "NVIDIA SEI metadata support library for GStreamer"
L4T_DEB_COPYRIGHT_MD5 = "cf5e527faa5ef67c91ec75b7f6c501e7"

DEPENDS = "glib-2.0 gstreamer1.0"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-gstreamer"
L4T_DEB_IS_COMMON = "1"

require tegra-debian-libraries-common.inc

MAINSUM = "008c8c2821be7fe8d4e5d8f41d1cebd38801e3dddf7912aeaf63da22a6cd7482"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libgstnvdsseimeta.so.1.0.0 \
"
do_install() {
    install_libraries
    ln -s libgstnvdsseimeta.so.1.0.0 ${D}${libdir}/libgstnvdsseimeta.so
}
