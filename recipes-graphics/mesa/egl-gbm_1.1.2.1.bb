DESCRIPTION = "EGL backend for libgbm"
HOMEPAGE = "https://github.com/NVIDIA/egl-gbm"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=6288a8dacdfbb0c5d8e4cf6dade6d203"
DEPENDS = "eglexternalplatform libdrm virtual/libgbm"

SRC_REPO = "github.com/NVIDIA/egl-gbm.git;protocol=https"
SRCBRANCH = "main"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH} \
           file://0001-gbm-display-handle-kms-display-only-devices-in-FindG.patch \
           "
# 1.1.2.1 tag
SRCREV = "b24587d4871a630d05e9e26da94c95e6ce4324f2"

REQUIRED_DISTRO_FEATURES = "opengl"

S = "${WORKDIR}/git"

inherit meson pkgconfig features_check

FILES:${PN} += "${datadir}/egl"
INSANE_SKIP:${PN} = "dev-so"
