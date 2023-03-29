DESCRIPTION = "NVIDIA compositor GStreamer plugin"
SECTION = "multimedia"
LICENSE = "BSD-3-Clause & Proprietary"
LIC_FILES_CHKSUM = "file://nvbuf_utils.h;endline=9;md5=e74e59ff8c4105650b55b3a26f41d7ac \
                    file://README.txt;endline=26;md5=d4da79f8cebc6b73ce481b090afa99ae \
"

TEGRA_SRC_SUBARCHIVE = "Linux_for_Tegra/source/public/gst-nvcompositor_src.tbz2"
require recipes-bsp/tegra-sources/tegra-sources-35.3.1.inc

DEPENDS = "gstreamer1.0 gstreamer1.0-plugins-base virtual/egl tegra-mmapi tegra-libraries-multimedia-utils"

SRC_URI += " file://0001-Update-makefile-for-OE-builds.patch \
             file://0002-Skip-map-frame-in-pad-prepare-to-fix-spurious-warnin.patch \
"

S = "${WORKDIR}/gst-nvcompositor"

inherit pkgconfig features_check

REQUIRED_DISTRO_FEATURES = "opengl"

do_install() {
	oe_runmake install DESTDIR="${D}"
}
FILES:${PN} = "${libdir}/gstreamer-1.0"
