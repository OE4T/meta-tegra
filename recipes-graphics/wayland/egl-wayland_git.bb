DESCRIPTION = "Wayland EGL external platform library"
HOMEPAGE = "https://github.com/NVIDIA/egl-wayland"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=cfa5a0f49cb081823fc5d965566e8298"
DEPENDS = "eglexternalplatform virtual/egl wayland wayland-protocols wayland-native libdrm"

SRC_REPO = "github.com/NVIDIA/egl-wayland.git;protocol=https"
SRCBRANCH = "master"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCREV = "067e43d0d4af82e4ea3fdc8ce476e6a24f69956f"
PV = "1.1.13+git"

SRC_URI += " \
    file://0001-Fix-wayland-eglstream-protocols-pc-file.patch \
    file://0001-wayland-eglsurface-Change-type-of-timout-from-EGLint.patch \
    file://nvidia_wayland.json \
"

REQUIRED_DISTRO_FEATURES = "opengl"

inherit meson pkgconfig features_check

do_install:append() {
    install -d ${D}${datadir}/egl/egl_external_platform.d
    install -m 0644 ${UNPACKDIR}/nvidia_wayland.json ${D}${datadir}/egl/egl_external_platform.d/20_nvidia_wayland.json
}

FILES:${PN} += "${datadir}/egl"
FILES:${PN}-dev += "${datadir}/wayland-eglstream"
