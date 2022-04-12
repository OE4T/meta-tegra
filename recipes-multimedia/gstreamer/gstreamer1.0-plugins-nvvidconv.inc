DESCRIPTION = "NVIDIA video converter GStreamer plugin"
SECTION = "multimedia"
LICENSE = "BSD-3-Clause & Proprietary"
LIC_FILES_CHKSUM = "file://nvbufsurface.h;endline=9;md5=7b9355c0d0bf82c6b3ca2dd270b27d38 \
                    file://README.txt;endline=26;md5=d4da79f8cebc6b73ce481b090afa99ae \
"

TEGRA_SRC_SUBARCHIVE = "Linux_for_Tegra/source/public/gst-nvvidconv_src.tbz2"
<<<<<<< HEAD
require recipes-bsp/tegra-sources/tegra-sources-32.7.2.inc
=======
require recipes-bsp/tegra-sources/tegra-sources-34.1.0.inc
>>>>>>> a7def3cc (gstreamer: update recipes for R34.1.0)

DEPENDS = "gstreamer1.0 glib-2.0 gstreamer1.0-plugins-base tegra-libraries-multimedia tegra-mmapi"

SRC_URI += "\
    file://0001-Update-makefile-for-OE-builds.patch \
    file://0002-Use-filter-function-for-fixating-caps.patch \
"

S = "${WORKDIR}/gst-nvvidconv"

inherit pkgconfig container-runtime-csv features_check

REQUIRED_DISTRO_FEATURES = "opengl"

CONTAINER_CSV_FILES = "${libdir}/gstreamer-1.0/*.so*"

do_install() {
	oe_runmake install DESTDIR="${D}"
}
FILES:${PN} = "${libdir}/gstreamer-1.0"