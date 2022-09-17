DESCRIPTION = "NVIDIA video tee GStreamer plugin"
SECTION = "multimedia"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://README.txt;endline=26;md5=d4da79f8cebc6b73ce481b090afa99ae"

TEGRA_SRC_SUBARCHIVE = "Linux_for_Tegra/source/public/gst-nvtee_src.tbz2"
<<<<<<<< HEAD:recipes-multimedia/gstreamer/gstreamer1.0-plugins-nvtee_1.14.0-r32.7.2.bb
require recipes-bsp/tegra-sources/tegra-sources-32.7.2.inc
========
require recipes-bsp/tegra-sources/tegra-sources-34.1.0.inc
>>>>>>>> a7def3cc (gstreamer: update recipes for R34.1.0):recipes-multimedia/gstreamer/gstreamer1.0-plugins-nvtee_1.14.0-r34.1.0.bb

DEPENDS = "gstreamer1.0 glib-2.0 gstreamer1.0-plugins-base"

SRC_URI += " file://0001-Update-makefile-for-OE-builds.patch"

S = "${WORKDIR}/gst-nvtee"

inherit pkgconfig container-runtime-csv

CONTAINER_CSV_FILES = "${libdir}/gstreamer-1.0/*.so*"

do_install() {
	oe_runmake install DESTDIR="${D}"
}
FILES:${PN} = "${libdir}/gstreamer-1.0"
