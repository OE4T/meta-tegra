DESCRIPTION = "EGL backend for libgbm"
HOMEPAGE = "https://github.com/NVIDIA/egl-gbm"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://COPYING;md5=6288a8dacdfbb0c5d8e4cf6dade6d203"
DEPENDS = "eglexternalplatform libdrm virtual/libgbm"

SRC_REPO = "github.com/NVIDIA/egl-gbm.git;protocol=https"
SRCBRANCH = "main"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
# 1.1.0 tag
SRCREV = "39932b2cc4f44cdadd553cc931f3bebd4e348d10"

SRC_URI += "\
    file://0001-Tegra-workarounds.patch \
    file://nvidia_gbm.json \
"

REQUIRED_DISTRO_FEATURES = "opengl"

S = "${WORKDIR}/git"

inherit meson pkgconfig features_check

do_install:append() {
    install -d ${D}${datadir}/egl/egl_external_platform.d
    install -m 0644 ${WORKDIR}/nvidia_gbm.json ${D}${datadir}/egl/egl_external_platform.d/15_nvidia_gbm.json
}

FILES:${PN} += "${datadir}/egl"
INSANE_SKIP:${PN} = "dev-so"
