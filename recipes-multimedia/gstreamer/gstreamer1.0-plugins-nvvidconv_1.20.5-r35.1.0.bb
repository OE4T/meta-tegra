DESCRIPTION = "NVIDIA video converter GStreamer plugin"
SECTION = "multimedia"
LICENSE = "BSD-3-Clause & Proprietary"
LIC_FILES_CHKSUM = "file://nvbufsurface.h;endline=9;md5=7b9355c0d0bf82c6b3ca2dd270b27d38 \
                    file://README.txt;endline=26;md5=d4da79f8cebc6b73ce481b090afa99ae \
"

TEGRA_SRC_SUBARCHIVE = "Linux_for_Tegra/source/public/gst-nvvidconv_src.tbz2"
require recipes-bsp/tegra-sources/tegra-sources-35.1.0.inc

DEPENDS = "gstreamer1.0 glib-2.0 gstreamer1.0-plugins-base tegra-libraries-multimedia tegra-mmapi cuda-driver cuda-cudart"

SRC_URI += "\
    file://0001-Update-makefile-for-OE-builds.patch \
    file://0002-Use-filter-function-for-fixating-caps.patch \
"

S = "${WORKDIR}/gst-nvvidconv"

inherit pkgconfig container-runtime-csv features_check

EXTRA_OEMAKE = "CUDA_VER=${CUDA_VERSION}"

REQUIRED_DISTRO_FEATURES = "opengl"

CONTAINER_CSV_FILES = "${libdir}/gstreamer-1.0/*.so*"

do_install() {
	oe_runmake install DESTDIR="${D}"
}
FILES:${PN} = "${libdir}/gstreamer-1.0"
