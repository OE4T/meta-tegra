DESCRIPTION = "NVIDIA v4l2 GStreamer plugin"
SECTION = "multimedia"
LICENSE = "LGPL-2.0-only & BSD-3-Clause & Proprietary"
LIC_FILES_CHKSUM = "file://LICENSE.gst-nvvideo4linux2;md5=457fb5d7ae2d8cd8cabcc21789a37e5c \
                    file://README.txt;endline=11;md5=71af624b03396c4f2c70c9c8684ff3d2 \
"

TEGRA_SRC_SUBARCHIVE = "Linux_for_Tegra/source/public/gst-nvvideo4linux2_src.tbz2"
require recipes-bsp/tegra-sources/tegra-sources-35.1.0.inc

SRC_URI += "file://0002-v4l2videoenc-Fix-negotiation-caps-leak.patch \
           file://0003-v4l2allocator-Fix-data-offset-bytesused-size-validat.patch \
           file://0004-v4l2bufferpool-Avoid-set_flushing-warning.patch \
           file://0005-gstv4l2videodec-use-ifdef-macro-for-consistency-with.patch \
           file://0006-gstv4l2videodec-check-if-we-have-a-pool-before-the-l.patch \
           file://0007-Fix-resource-leak-in-nvv4l2decoder.patch \
           file://0001-Work-around-missing-nvdsseimeta-header.patch \
           file://0008-Makefile-fixes-for-OE-builds.patch \
           "

DEPENDS = "gstreamer1.0 glib-2.0 gstreamer1.0-plugins-base virtual/egl tegra-libraries-multimedia tegra-mmapi"

PACKAGECONFIG ??= "libv4l2"
PACKAGECONFIG[libv4l2] = ",,v4l-utils,tegra-libraries-multimedia-v4l"
EXTRA_OEMAKE = "${@bb.utils.contains('PACKAGECONFIG', 'libv4l2', 'USE_LIBV4L2=1', '', d)}"
CFLAGS += "-fcommon"

S = "${WORKDIR}/gst-v4l2"

inherit gettext pkgconfig container-runtime-csv features_check

REQUIRED_DISTRO_FEATURES = "opengl"

CONTAINER_CSV_FILES = "${libdir}/gstreamer-1.0/*.so*"

remove_headers() {
	rm ${WORKDIR}/nvbuf_utils.h 
	rm ${WORKDIR}/nvbufsurface.h
	rm ${WORKDIR}/v4l2_nv_extensions.h
}

do_unpack:append() {
    if not bb.data.inherits_class("externalsrc", d):
        bb.build.exec_func("remove_headers", d)
}

do_install() {
	oe_runmake install DESTDIR="${D}"
}
FILES:${PN} = "${libdir}/gstreamer-1.0"
