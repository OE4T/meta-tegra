require recipes-multimedia/gstreamer/gstreamer1.0-plugins.inc

LICENSE = "GPLv2+ & LGPLv2+ & MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=73a5855a8119deb017f5f13cf327095d \
                    file://COPYING.LIB;md5=21682e4e8fea52413fd26c60acb907e5 \
                    file://ext/eglgles/gstegladaptation.c;beginline=9;endline=25;md5=51eafe984c428127773b6a95eb959d0b"

SRC_URI = "http://developer.download.nvidia.com/embedded/L4T/r23_Release_v1.0/source/gstegl_src.tbz2"
SRC_URI[md5sum] = "7503a58cf2f8b923b8c7e8468624dce8"
SRC_URI[sha256sum] = "be237a274f710a21623bacbc933682cbf67e31dc61ee5369083cd474fdbee6db"

DEPENDS += "gstreamer1.0-plugins-base virtual/egl virtual/libgles2 libx11 libxext"

S = "${WORKDIR}/gstegl_src/gst-egl"

inherit gettext

do_install_append() {
    sed -i -e's,${STAGING_INCDIR},${includedir},g' ${D}${libdir}/pkgconfig/gstreamer-egl-1.0.pc
}