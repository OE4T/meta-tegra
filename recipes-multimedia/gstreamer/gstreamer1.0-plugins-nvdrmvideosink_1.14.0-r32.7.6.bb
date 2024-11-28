DESCRIPTION = "NVIDIA DRM video sink GStreamer plugin"
SECTION = "multimedia"
LICENSE = "BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE.libgstnvdrmvideosink;md5=674ef4559ff709167b72104cb9814e93"

TEGRA_SRC_SUBARCHIVE = "Linux_for_Tegra/source/public/libgstnvdrmvideosink_src.tbz2"
require recipes-bsp/tegra-sources/tegra-sources-32.7.6.inc

DEPENDS = "gstreamer1.0 gstreamer1.0-plugins-base virtual/egl libdrm tegra-libraries-multimedia-utils tegra-mmapi nvbufsurface-headers"

SRC_URI += "\
    file://0001-Update-makefile-for-OE-builds.patch \
    file://0002-Fix-screen-blanking-when-stopping-pipeline.patch \
"

S = "${WORKDIR}/gst-nvdrmvideosink"

inherit pkgconfig container-runtime-csv features_check

REQUIRED_DISTRO_FEATURES = "opengl"

CONTAINER_CSV_LIB_PATH = "/usr/lib/aarch64-linux-gnu/"

CONTAINER_CSV_FILES = "${libdir}/gstreamer-1.0/*.so*"

CFLAGS:append = " -fpic"

do_install() {
	oe_runmake install DESTDIR="${D}"
}
FILES:${PN} = "${libdir}/gstreamer-1.0"
