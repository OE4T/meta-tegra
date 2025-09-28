DESCRIPTION = "NVIDIA-modified IPC pipeline GStreamer plugin"
SECTION = "multimedia"
LICENSE = "LGPL-2.1-only & BSD-3-Clause & Proprietary"
LIC_FILES_CHKSUM = "file://LICENSE;md5=aba133faf68bf941bf17b32a0b50f699 \
                    file://gstnvipcbufferpool.h;endline=27;md5=a3cdb0b800f9fb8ddbc7169f4254febc \
                    file://gstnvipcbufferpool.c;endline=10;md5=3f7bca09d7dd300b39cb2536807de1be"

TEGRA_SRC_SUBARCHIVE = "Linux_for_Tegra/source/gst-nvipcpipeline_src.tbz2"
require recipes-bsp/tegra-sources/tegra-sources-38.2.0.inc

COMPATIBLE_MACHINE = "(tegra)"

DEPENDS = "gstreamer1.0 glib-2.0 gstreamer1.0-plugins-base tegra-mmapi"

SRC_URI += " file://0001-Update-makefile-for-OE-builds.patch"
S = "${UNPACKDIR}/gst-nvipcpipeline"

inherit pkgconfig

do_install() {
	oe_runmake install DESTDIR="${D}"
}
FILES:${PN} = "${libdir}/gstreamer-1.0"
