DESCRIPTION = "NVIDIA video sinks GStreamer plugin"
SECTION = "multimedia"
LICENSE = "LGPL-2.0-only & Proprietary"
LIC_FILES_CHKSUM = "file://LICENSE.libgstnvvideosinks;md5=86ed1f32df3aaa376956e408540c024b \
                    file://README.txt;endline=11;md5=3670d068ae876bb6ea4c18beae2397ff \
"

TEGRA_SRC_SUBARCHIVE = "Linux_for_Tegra/source/public/libgstnvvideosinks_src.tbz2"
require recipes-bsp/tegra-sources/tegra-sources-32.7.6.inc

SRC_URI += "file://build-fixups.patch"

DEPENDS = "gstreamer1.0 glib-2.0 gstreamer1.0-plugins-base virtual/egl \
           tegra-libraries-multimedia tegra-libraries-multimedia-utils \
           gstreamer1.0-plugins-nveglgles tegra-mmapi nvbufsurface-headers"

REQUIRED_DISTRO_FEATURES = "x11 opengl"

S = "${WORKDIR}/gst-plugins-nv-video-sinks"

inherit gettext pkgconfig container-runtime-csv cuda features_check

CONTAINER_CSV_LIB_PATH = "/usr/lib/aarch64-linux-gnu/"

CONTAINER_CSV_FILES = "${libdir}/gstreamer-1.0/*.so*"

EXTRA_OEMAKE = "CUDA_VER=${CUDA_VERSION}"

do_install() {
	oe_runmake install DEST_DIR="${D}${libdir}/gstreamer-1.0"
}
FILES:${PN} = "${libdir}/gstreamer-1.0"
