DESCRIPTION = "NVIDIA XLib and XCB EGL Platform Library"
HOMEPAGE = "https://github.com/NVIDIA/egl-x11"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"
DEPENDS = "eglexternalplatform virtual/egl libdrm virtual/libgbm libx11 libxcb"

SRC_REPO = "github.com/NVIDIA/egl-x11.git;protocol=https"
SRCBRANCH = "main"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCREV = "5dc860774a201ee6b90ce45dc4c70f1c2a6c419a"
PV = "1.0.3"

REQUIRED_DISTRO_FEATURES = "opengl x11 wayland"

inherit meson pkgconfig features_check

FILES:${PN} += "${datadir}/egl"
