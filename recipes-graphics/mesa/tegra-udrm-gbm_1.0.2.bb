DESCRIPTION = "Tegra GBM backend for mesa"
HOMEPAGE = "https://github.com/oe4t/tegra-udrm-gbm"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c43c2c3b83cc7c8460566fb6da10f02a"

RPROVIDES:${PN} += "tegra-gbm-backend"

DEPENDS = "mesa libdrm"
RDEPENDS:${PN} = "tegra-libraries-gbm"

COMPATIBLE_MACHINE = "(tegra)"

SRC_REPO = "github.com/oe4t/tegra-udrm-gbm.git;protocol=https"
SRCBRANCH = "master"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
# v1.0.2 tag
SRCREV = "103d33d07e6c38106bf952403b336dc3ca809a08"

S = "${WORKDIR}/git"

inherit meson pkgconfig

do_install:append() {
    ln -s tegra-udrm_gbm.so ${D}${libdir}/gbm/nvidia-drm_gbm.so
}

FILES:${PN} += "${libdir}/gbm"
INSANE_SKIP:${PN} += "dev-so"
