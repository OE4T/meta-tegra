DESCRIPTION = "NVIDIA compositor GStreamer plugin"
SECTION = "multimedia"
LICENSE = "BSD-3-Clause & Proprietary"
LIC_FILES_CHKSUM = "file://nvbuf_utils.h;endline=9;md5=afc209f3955d083a93f5009bc9a65a22 \
                    file://README.txt;endline=26;md5=d4da79f8cebc6b73ce481b090afa99ae \
"

TEGRA_SRC_SUBARCHIVE = "Linux_for_Tegra/source/public/gst-nvcompositor_src.tbz2"
require recipes-bsp/tegra-sources/tegra-sources-32.5.2.inc

DEPENDS = "gstreamer1.0 gstreamer1.0-plugins-base gstreamer1.0-plugins-bad virtual/egl tegra-libraries"

SRC_URI += " file://0001-Update-makefile-for-OE-builds.patch"
S = "${WORKDIR}/gst-nvcompositor"

inherit pkgconfig container-runtime-csv

CONTAINER_CSV_FILES = "${libdir}/gstreamer-1.0/*.so*"

do_install() {
	oe_runmake install DESTDIR="${D}"
}
FILES_${PN} = "${libdir}/gstreamer-1.0"
