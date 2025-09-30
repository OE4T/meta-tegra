DESCRIPTION = "NVIDIA nvarguscamerasrc GStreamer plugin"
SECTION = "multimedia"
LICENSE = "BSD-3-Clause & Proprietary"
LIC_FILES_CHKSUM = "file://nvbufsurface.h;endline=9;md5=b973ef2bfaa397b1e255c76c2d2cb64b \
                    file://README.txt;endline=25;md5=364434949752edc42711344c8401d55b \
"

TEGRA_SRC_SUBARCHIVE = "Linux_for_Tegra/source/gst-nvarguscamera_src.tbz2"
TEGRA_SRC_SUBARCHIVE_OPTS = "--exclude=3rdpartyheaders.tbz2"

require recipes-bsp/tegra-sources/tegra-sources-38.2.1.inc

COMPATIBLE_MACHINE = "(tegra)"

SRC_URI += "\
    file://0001-Build-fixups.patch \
"

DEPENDS = "gstreamer1.0 glib-2.0 gstreamer1.0-plugins-base virtual/egl tegra-libraries-camera tegra-libraries-multimedia-ds tegra-mmapi"

S = "${UNPACKDIR}/gst-nvarguscamera"

inherit pkgconfig features_check

REQUIRED_DISTRO_FEATURES = "opengl"

do_install() {
	oe_runmake install DESTDIR="${D}"
}
FILES:${PN} = "${libdir}/gstreamer-1.0"
