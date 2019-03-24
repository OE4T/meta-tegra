DESCRIPTION = "NVIDIA v4l2 GStreamer plugin"
HOMEPAGE = "https://developer.nvidia.com/embedded/linux-tegra"
SECTION = "multimedia"
LICENSE = "LGPLv2 & BSD-3-Clause & Proprietary"
LIC_FILES_CHKSUM = "file://LICENSE.gst-nvvideo4linux2;md5=457fb5d7ae2d8cd8cabcc21789a37e5c \
                    file://nvbuf_utils.h;endline=9;md5=16cb2cc21933804415d51f3c64b38170 \
                    file://v4l2_nv_extensions.h;endline=28;md5=3ce6dfec705361908c7019a3b0eaf2f4 \
                    file://README.txt;endline=11;md5=71af624b03396c4f2c70c9c8684ff3d2 \
"

TEGRA_SRC_SUBARCHIVE = "public_sources/gst-nvvideo4linux2_src.tbz2"
require recipes-bsp/tegra-sources/tegra-sources-32.1.0.inc

SRC_URI += "file://build-fixups.patch"

DEPENDS = "gstreamer1.0 glib-2.0 gstreamer1.0-plugins-base virtual/egl tegra-libraries"

PACKAGECONFIG ??= "libv4l2"
PACKAGECONFIG[libv4l2] = ",,v4l-utils"
EXTRA_OEMAKE = "${@bb.utils.contains('PACKAGECONFIG', 'libv4l2', 'USE_LIBV4L2=1', '', d)}"

S = "${WORKDIR}/gst-v4l2"

inherit gettext pkgconfig

copy_headers() {
	cp ${WORKDIR}/nvbuf_utils.h ${S}/
	cp ${WORKDIR}/v4l2_nv_extensions.h ${S}/
}

do_unpack_append() {
    bb.build.exec_func("copy_headers", d)
}

do_install() {
	oe_runmake install DESTDIR="${D}"
}
FILES_${PN} = "${libdir}/gstreamer-1.0"
