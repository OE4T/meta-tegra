DESCRIPTION = "NVIDIA nvarguscamerasrc GStreamer plugin"
SECTION = "multimedia"
LICENSE = "BSD-3-Clause & Proprietary"
LIC_FILES_CHKSUM = "file://nvbufsurface.h;endline=9;md5=ba9f51385aebf755c973fc0a6b2277ad \
                    file://README.txt;endline=25;md5=364434949752edc42711344c8401d55b \
"

TEGRA_SRC_SUBARCHIVE = "Linux_for_Tegra/source/gst-nvarguscamera_src.tbz2"
TEGRA_SRC_SUBARCHIVE_OPTS = "--exclude=3rdpartyheaders.tbz2"

require recipes-bsp/tegra-sources/tegra-sources-36.4.4.inc

SRC_URI += "\
    file://0001-Build-fixups.patch \
"

DEPENDS = "gstreamer1.0 glib-2.0 gstreamer1.0-plugins-base virtual/egl tegra-libraries-camera tegra-libraries-multimedia-ds tegra-mmapi"

S = "${WORKDIR}/gst-nvarguscamera"

inherit pkgconfig features_check

REQUIRED_DISTRO_FEATURES = "opengl"

do_install() {
	oe_runmake install DESTDIR="${D}"
}
FILES:${PN} = "${libdir}/gstreamer-1.0"
