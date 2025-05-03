DESCRIPTION = "NVIDIA video tee GStreamer plugin"
SECTION = "multimedia"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://README.txt;endline=26;md5=d4da79f8cebc6b73ce481b090afa99ae"

TEGRA_SRC_SUBARCHIVE = "Linux_for_Tegra/source/public/gst-nvtee_src.tbz2"
require recipes-bsp/tegra-sources/tegra-sources-35.6.1.inc

DEPENDS = "gstreamer1.0 glib-2.0 gstreamer1.0-plugins-base"

SRC_URI += " file://0001-Update-makefile-for-OE-builds.patch"

S = "${WORKDIR}/gst-nvtee"

inherit pkgconfig

do_install() {
	oe_runmake install DESTDIR="${D}"
}
FILES:${PN} = "${libdir}/gstreamer-1.0"
