DESCRIPTION = "NVIDIA video sinks GStreamer plugin"
HOMEPAGE = "https://developer.nvidia.com/embedded/linux-tegra"
SECTION = "multimedia"
LICENSE = "LGPLv2 & Proprietary"
LIC_FILES_CHKSUM = "file://LICENSE.libgstnvvideosinks;md5=86ed1f32df3aaa376956e408540c024b \
                    file://nvbuf_utils.h;endline=9;md5=66db9eb40c77d118dc0faa3d76da49a1 \
                    file://README.txt;endline=11;md5=d0b0c459af10bc6ecd665c01d08d0650 \
"

TEGRA_SRC_SUBARCHIVE = "public_sources/libgstnvvideosinks_src.tbz2"
require recipes-bsp/tegra-sources/tegra-sources-32.1.0.inc

SRC_URI += "file://build-fixups.patch"

DEPENDS = "gstreamer1.0 glib-2.0 gstreamer1.0-plugins-base virtual/egl tegra-libraries"


S = "${WORKDIR}/gst-plugins-nv-video-sinks"

inherit gettext pkgconfig

copy_headers() {
	cp ${WORKDIR}/nvbuf_utils.h ${S}/
}

do_unpack_append() {
    bb.build.exec_func("copy_headers", d)
}

do_install() {
	oe_runmake install DESTDIR="${D}"
}
FILES_${PN} = "${libdir}/gstreamer-1.0"
