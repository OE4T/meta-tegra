DESCRIPTION = "Wayland EGL external platform library"
HOMEPAGE = "https://github.com/NVIDIA/egl-wayland"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=cfa5a0f49cb081823fc5d965566e8298"
DEPENDS = "eglexternalplatform mesa wayland wayland-protocols wayland-native"

SRC_URI = "git://github.com/NVIDIA/egl-wayland;protocol=https"
SRCREV = "1676d1d2bfe646fb3ecbba29a85db529dd399493"
SRC_URI += "file://nvidia_wayland.json"

S = "${WORKDIR}/git"

inherit autotools-brokensep pkgconfig

do_install_append() {
    install -d ${D}${datadir}/egl/egl_external_platform.d
    install -m 0644 ${WORKDIR}/nvidia_wayland.json ${D}${datadir}/egl/egl_external_platform.d/
}

FILES_${PN} += "${datadir}/egl"
FILES_${PN}-dev += "${datadir}/wayland-eglstream"


