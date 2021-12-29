DESCRIPTION = "NVIDIA SEI metadata support library for GStreamer"
L4T_DEB_COPYRIGHT_MD5 = "e9ada95d2bb512af0bf751941ce6e6ba"

DEPENDS = "glib-2.0 gstreamer1.0"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-gstreamer"

require tegra-debian-libraries-common.inc

MAINSUM = "c57247c840ef77743d3f3054c4cf7e88d8b3bef91e65919ef7f3005bd3f4bc3a"

TEGRA_LIBRARIES_TO_INSTALL = "\
    tegra/libgstnvdsseimeta.so.1.0.0 \
"
do_install() {
    install_libraries
    ln -s libgstnvdsseimeta.so.1.0.0 ${D}${libdir}/libgstnvdsseimeta.so
}

CONTAINER_CSV_FILES = "${libdir}/*.so*"
