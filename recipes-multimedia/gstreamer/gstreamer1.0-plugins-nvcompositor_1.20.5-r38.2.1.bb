DESCRIPTION = "NVIDIA compositor GStreamer plugin"
SECTION = "multimedia"
LICENSE = "BSD-3-Clause & Proprietary"
LIC_FILES_CHKSUM = "file://gstnvcompositor.h;beginline=64;endline=64;md5=4bc2fab8a765b0fc125c88f3b3c38d2f \
                    file://README.txt;endline=26;md5=d4da79f8cebc6b73ce481b090afa99ae \
"

TEGRA_SRC_SUBARCHIVE = "Linux_for_Tegra/source/gst-nvcompositor_src.tbz2"
TEGRA_SRC_SUBARCHIVE_OPTS = "--exclude=3rdpartyheaders.tbz2"
require recipes-bsp/tegra-sources/tegra-sources-38.2.1.inc

COMPATIBLE_MACHINE = "(tegra)"

DEPENDS = "gstreamer1.0 gstreamer1.0-plugins-base virtual/egl tegra-mmapi tegra-libraries-multimedia-utils cuda-cudart"

SRC_URI += " file://0001-Update-makefile-for-OE-builds.patch"

S = "${UNPACKDIR}/gst-nvcompositor"

inherit pkgconfig features_check

EXTRA_OEMAKE = "CUDA_VER=${CUDA_VERSION}"

REQUIRED_DISTRO_FEATURES = "opengl"

do_install() {
	oe_runmake install DESTDIR="${D}"
}
FILES:${PN} = "${libdir}/gstreamer-1.0"
