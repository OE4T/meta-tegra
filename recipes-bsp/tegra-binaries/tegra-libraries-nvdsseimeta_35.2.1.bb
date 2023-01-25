DESCRIPTION = "NVIDIA SEI metadata support library for GStreamer"
L4T_DEB_COPYRIGHT_MD5 = "e9ada95d2bb512af0bf751941ce6e6ba"

DEPENDS = "glib-2.0 gstreamer1.0"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-gstreamer"

require tegra-debian-libraries-common.inc

MAINSUM = "76567c73f13ddf0cc837caa5c143bcf447b5667f28e18a7108df10868476f4b0"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libgstnvdsseimeta.so.1.0.0 \
"
do_install() {
    install_libraries
    ln -s libgstnvdsseimeta.so.1.0.0 ${D}${libdir}/libgstnvdsseimeta.so
}
