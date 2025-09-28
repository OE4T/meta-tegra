DESCRIPTION = "NVIDIA-modified unixfd GStreamer plugin"
SECTION = "multimedia"
LICENSE = "LGPL-2.1-only & Proprietary"
LIC_FILES_CHKSUM = "file://LICENSE;md5=7c5177a0e72658a405eafbb17693804b \
                    file://gstnvipcbufferpool.h;endline=10;md5=d9dae5d6e4678fa3c11acc234577e28c"

TEGRA_SRC_SUBARCHIVE = "Linux_for_Tegra/source/gst-nvunixfd_src.tbz2"
require recipes-bsp/tegra-sources/tegra-sources-38.2.0.inc

COMPATIBLE_MACHINE = "(tegra)"

DEPENDS = "gstreamer1.0 glib-2.0 gstreamer1.0-plugins-base tegra-mmapi cuda-cudart"

SRC_URI += " file://0001-Update-makefile-for-OE-builds.patch"
S = "${UNPACKDIR}/gst-nvunixfd"

inherit pkgconfig

EXTRA_OEMAKE = "CUDA_VER=${CUDA_VERSION}"

do_install() {
	oe_runmake install DESTDIR="${D}"
}
FILES:${PN} = "${libdir}/gstreamer-1.0"
