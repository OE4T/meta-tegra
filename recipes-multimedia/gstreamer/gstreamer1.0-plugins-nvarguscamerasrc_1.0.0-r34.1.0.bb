DESCRIPTION = "NVIDIA nvarguscamerasrc GStreamer plugin"
SECTION = "multimedia"
LICENSE = "BSD-3-Clause & Proprietary"
LIC_FILES_CHKSUM = "file://nvbuf_utils.h;endline=9;md5=e74e59ff8c4105650b55b3a26f41d7ac \
                    file://README.txt;endline=25;md5=364434949752edc42711344c8401d55b \
"

TEGRA_SRC_SUBARCHIVE = "Linux_for_Tegra/source/public/gst-nvarguscamera_src.tbz2"
TEGRA_SRC_SUBARCHIVE_OPTS = "--exclude=3rdpartyheaders.tbz2"

<<<<<<<< HEAD:recipes-multimedia/gstreamer/gstreamer1.0-plugins-nvarguscamerasrc_1.0.0-r32.7.2.bb
require recipes-bsp/tegra-sources/tegra-sources-32.7.2.inc
========
require recipes-bsp/tegra-sources/tegra-sources-34.1.0.inc
>>>>>>>> a7def3cc (gstreamer: update recipes for R34.1.0):recipes-multimedia/gstreamer/gstreamer1.0-plugins-nvarguscamerasrc_1.0.0-r34.1.0.bb

SRC_URI += "\
    file://0001-Build-fixups.patch \
    file://0002-Fix-nvarguscamerasrc-build-errors.patch \
"

DEPENDS = "gstreamer1.0 glib-2.0 gstreamer1.0-plugins-base virtual/egl tegra-libraries-camera tegra-mmapi"

S = "${WORKDIR}/gst-nvarguscamera"

inherit pkgconfig container-runtime-csv features_check

REQUIRED_DISTRO_FEATURES = "opengl"

CONTAINER_CSV_FILES = "${libdir}/gstreamer-1.0/*.so*"

do_install() {
	oe_runmake install DESTDIR="${D}"
}
FILES:${PN} = "${libdir}/gstreamer-1.0"
