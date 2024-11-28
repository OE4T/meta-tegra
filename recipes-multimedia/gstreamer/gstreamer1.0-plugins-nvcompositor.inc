DESCRIPTION = "NVIDIA compositor GStreamer plugin"
SECTION = "multimedia"
LICENSE = "BSD-3-Clause & Proprietary"
LIC_FILES_CHKSUM = "file://nvbuf_utils.h;endline=9;md5=ab96318bfad94a30f526381fddc42abb \
                    file://README.txt;endline=26;md5=d4da79f8cebc6b73ce481b090afa99ae \
"

TEGRA_SRC_SUBARCHIVE = "Linux_for_Tegra/source/public/gst-nvcompositor_src.tbz2"
require recipes-bsp/tegra-sources/tegra-sources-32.7.6.inc

DEPENDS = "gstreamer1.0 gstreamer1.0-plugins-base gstreamer1.0-plugins-bad virtual/egl tegra-libraries-multimedia-utils"

SRC_URI += " file://0001-Update-makefile-for-OE-builds.patch"

S = "${WORKDIR}/gst-nvcompositor"

inherit pkgconfig container-runtime-csv features_check

REQUIRED_DISTRO_FEATURES = "opengl"

CONTAINER_CSV_LIB_PATH = "/usr/lib/aarch64-linux-gnu/"

CONTAINER_CSV_FILES = "${libdir}/gstreamer-1.0/*.so*"

do_install() {
	oe_runmake install DESTDIR="${D}"
}
FILES:${PN} = "${libdir}/gstreamer-1.0"
