DESCRIPTION = "NVIDIA DRM video sink GStreamer plugin"
SECTION = "multimedia"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.libgstnvdrmvideosink;md5=674ef4559ff709167b72104cb9814e93"

TEGRA_SRC_SUBARCHIVE = "Linux_for_Tegra/source/public/libgstnvdrmvideosink_src.tbz2"
<<<<<<<< HEAD:recipes-multimedia/gstreamer/gstreamer1.0-plugins-nvdrmvideosink_1.14.0-r32.7.2.bb
require recipes-bsp/tegra-sources/tegra-sources-32.7.2.inc
========
require recipes-bsp/tegra-sources/tegra-sources-34.1.0.inc
>>>>>>>> a7def3cc (gstreamer: update recipes for R34.1.0):recipes-multimedia/gstreamer/gstreamer1.0-plugins-nvdrmvideosink_1.14.0-r34.1.0.bb

SRC_URI += " \
    file://0001-Work-around-lack-of-nvsocsysapi-header.patch \
    file://0001-Update-makefile-for-OE-builds.patch \
"

DEPENDS = "gstreamer1.0 gstreamer1.0-plugins-base virtual/egl libdrm tegra-libraries-multimedia-utils tegra-mmapi"

S = "${WORKDIR}/gst-nvdrmvideosink"

inherit pkgconfig container-runtime-csv features_check

REQUIRED_DISTRO_FEATURES = "opengl"

CONTAINER_CSV_FILES = "${libdir}/gstreamer-1.0/*.so*"

CFLAGS:append = " -fpic"

do_install() {
	oe_runmake install DESTDIR="${D}"
}
FILES:${PN} = "${libdir}/gstreamer-1.0"
