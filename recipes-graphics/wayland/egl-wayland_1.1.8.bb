DESCRIPTION = "Wayland EGL external platform library"
HOMEPAGE = "https://github.com/NVIDIA/egl-wayland"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=cfa5a0f49cb081823fc5d965566e8298"
DEPENDS = "eglexternalplatform virtual/egl wayland wayland-protocols wayland-native"

SRC_URI = "git://github.com/NVIDIA/egl-wayland"
# tag 1.1.8
SRCREV = "e7ac2ab3f0c516bcbd235af2995830d7f06f72a6"
SRC_URI += " \
    file://0001-Fix-wayland-eglstream-protocols-pc-file.patch \
    file://nvidia_wayland.json \
"

S = "${WORKDIR}/git"

inherit meson pkgconfig

do_install:append() {
    install -d ${D}${datadir}/egl/egl_external_platform.d
    install -m 0644 ${WORKDIR}/nvidia_wayland.json ${D}${datadir}/egl/egl_external_platform.d/
}

FILES:${PN} += "${datadir}/egl"
FILES:${PN}-dev += "${datadir}/wayland-eglstream"


