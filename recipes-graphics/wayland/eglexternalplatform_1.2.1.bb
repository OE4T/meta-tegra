DESCRIPTION = "EGL external platform interface"
HOMEPAGE = "https://github.com/NVIDIA/eglexternalplatform"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=cfa5a0f49cb081823fc5d965566e8298"

SRC_URI = "git://github.com/NVIDIA/eglexternalplatform.git;protocol=https;branch=master"
# corresponds to 1.2.1 tag
SRCREV = "cf9f10589775c1b4d90f1df6f417f8cf1bed7ec6"

inherit meson

ALLOW_EMPTY:${PN} = "1"
FILES:${PN}-dev = "${includedir} ${datadir}/pkgconfig"

