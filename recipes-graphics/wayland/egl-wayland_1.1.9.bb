DESCRIPTION = "Wayland EGL external platform library"
HOMEPAGE = "https://github.com/NVIDIA/egl-wayland"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=cfa5a0f49cb081823fc5d965566e8298"
DEPENDS = "eglexternalplatform virtual/egl wayland wayland-protocols wayland-native"

SRC_REPO = "github.com/NVIDIA/egl-wayland.git;protocol=https"
SRCBRANCH = "master"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
# tag 1.1.9 + 3 commits post-tag
SRCREV = "582b2d345abaa0e313cf16c902e602084ea59551"
PV .= "+3"

SRC_URI += " \
    file://0001-Fix-wayland-eglstream-protocols-pc-file.patch \
    file://nvidia_wayland.json \
"

REQUIRED_DISTRO_FEATURES = "opengl"

S = "${WORKDIR}/git"

inherit meson pkgconfig features_check

do_install:append() {
    install -d ${D}${datadir}/egl/egl_external_platform.d
    install -m 0644 ${WORKDIR}/nvidia_wayland.json ${D}${datadir}/egl/egl_external_platform.d/
}

FILES:${PN} += "${datadir}/egl"
FILES:${PN}-dev += "${datadir}/wayland-eglstream"
