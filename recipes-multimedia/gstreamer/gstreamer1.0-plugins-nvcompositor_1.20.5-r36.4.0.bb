DESCRIPTION = "NVIDIA compositor GStreamer plugin"
SECTION = "multimedia"
LICENSE = "BSD-3-Clause & Proprietary"
LIC_FILES_CHKSUM = "file://gstnvcompositor.h;beginline=64;endline=64;md5=7d1c171edbf503035189a0f237588e6b \
                    file://README.txt;endline=26;md5=d4da79f8cebc6b73ce481b090afa99ae \
"

TEGRA_SRC_SUBARCHIVE = "Linux_for_Tegra/source/gst-nvcompositor_src.tbz2"
TEGRA_SRC_SUBARCHIVE_OPTS = "--exclude=3rdpartyheaders.tbz2"
require recipes-bsp/tegra-sources/tegra-sources-36.4.0.inc

DEPENDS = "gstreamer1.0 gstreamer1.0-plugins-base virtual/egl tegra-mmapi tegra-libraries-multimedia-utils"

SRC_URI += " file://0001-Update-makefile-for-OE-builds.patch"

S = "${WORKDIR}/gst-nvcompositor"

inherit pkgconfig features_check

REQUIRED_DISTRO_FEATURES = "opengl"

do_install() {
	oe_runmake install DESTDIR="${D}"
}
FILES:${PN} = "${libdir}/gstreamer-1.0"
