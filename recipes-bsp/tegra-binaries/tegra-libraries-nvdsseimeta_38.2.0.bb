DESCRIPTION = "NVIDIA SEI metadata support library for GStreamer"
L4T_DEB_COPYRIGHT_MD5 = "cf5e527faa5ef67c91ec75b7f6c501e7"

DEPENDS = "glib-2.0 gstreamer1.0"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-gstreamer"
L4T_DEB_IS_COMMON = "1"

require tegra-debian-libraries-common.inc

MAINSUM = "c1ed76fd652e5a540be17facde291858885f6bb84862ef22776085b9da857650"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libgstnvdsseimeta.so.1.0.0 \
"
do_install() {
    install_libraries
    ln -s libgstnvdsseimeta.so.1.0.0 ${D}${libdir}/libgstnvdsseimeta.so
}
