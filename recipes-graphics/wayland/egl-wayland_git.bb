DESCRIPTION = "Wayland EGL external platform library"
HOMEPAGE = "https://github.com/NVIDIA/egl-wayland"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=cfa5a0f49cb081823fc5d965566e8298"
DEPENDS = "eglexternalplatform virtual/egl wayland wayland-protocols wayland-native libdrm"

SRC_REPO = "github.com/NVIDIA/egl-wayland.git;protocol=https"
SRCBRANCH = "master"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCREV = "a557bed172dcd7ba379095f784d047202281c0de"
PV = "1.1.22+git"

SRC_URI += " \
    file://0001-Fix-wayland-eglstream-protocols-pc-file.patch \
"

REQUIRED_DISTRO_FEATURES = "opengl"

inherit meson pkgconfig features_check

FILES:${PN} += "${datadir}/egl"
FILES:${PN}-dev += "${datadir}/wayland-eglstream"
