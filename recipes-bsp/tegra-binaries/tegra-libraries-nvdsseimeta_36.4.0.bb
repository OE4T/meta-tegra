DESCRIPTION = "NVIDIA SEI metadata support library for GStreamer"
L4T_DEB_COPYRIGHT_MD5 = "20bbd485b9b57fbc0b55d6efc08e3f4a"

DEPENDS = "glib-2.0 gstreamer1.0"

require tegra-debian-libraries-common.inc

SRC_COMMON_DEBS += "${@l4t_deb_pkgname(d, 'gstreamer')};subdir=${BP};name=gstreamer"

GSTSUM = "3c792e88ff0b5ae24c20f570574d834d8c3f22a0c51a53e734b2b6b15d6dbf5f"
MAINSUM = "3c792e88ff0b5ae24c20f570574d834d8c3f22a0c51a53e734b2b6b15d6dbf5f"

SRC_URI[gstreamer.sha256sum] = "${GSTSUM}"

TEGRA_LIBRARIES_TO_INSTALL = "\
    nvidia/libgstnvdsseimeta.so.1.0.0 \
"
do_install() {
    install_libraries
    ln -s libgstnvdsseimeta.so.1.0.0 ${D}${libdir}/libgstnvdsseimeta.so
}
