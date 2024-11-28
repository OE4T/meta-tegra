DESCRIPTION = "NVIDIA v4l2camerasrc GStreamer plugin"
SECTION = "multimedia"
LICENSE = "BSD-3-Clause & Proprietary"
LIC_FILES_CHKSUM = "file://nvbuf_utils.h;endline=9;md5=ab96318bfad94a30f526381fddc42abb \
                    file://README.txt;endline=25;md5=afc286435ccd143c9a10b5d7a8c1dee1 \
"

TEGRA_SRC_SUBARCHIVE = "Linux_for_Tegra/source/public/gst-nvv4l2camera_src.tbz2"
TEGRA_SRC_SUBARCHIVE_OPTS = "--exclude=3rdpartyheaders.tbz2"
TEGRA_SRC_EXTRA_SUBARCHIVE = "Linux_for_Tegra/source/public/gst-nvarguscamera_src.tbz2"
TEGRA_SRC_EXTRA_SUBARCHIVE_OPTS = "-C ${S} --strip-components=1 gst-nvarguscamera/nvbufsurface.h"

require recipes-bsp/tegra-sources/tegra-sources-32.7.6.inc

SRC_URI += "\
    file://0001-Build-fixups.patch \
    file://0002-Clean-up-compiler-warnings.patch \
"

DEPENDS = "gstreamer1.0 glib-2.0 gstreamer1.0-plugins-base gstreamer1.0-plugins-good libv4l virtual/egl tegra-libraries-multimedia"

S = "${WORKDIR}/gst-nvv4l2camera"

inherit pkgconfig container-runtime-csv features_check

REQUIRED_DISTRO_FEATURES = "opengl"

CONTAINER_CSV_LIB_PATH = "/usr/lib/aarch64-linux-gnu/"

CONTAINER_CSV_FILES = "${libdir}/gstreamer-1.0/*.so*"

do_install() {
	oe_runmake install DESTDIR="${D}"
}
FILES:${PN} = "${libdir}/gstreamer-1.0"
