DESCRIPTION = "Tegra GBM backend for mesa"
HOMEPAGE = "https://github.com/oe4t/tegra-udrm-gbm"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c43c2c3b83cc7c8460566fb6da10f02a"

DEPENDS = "mesa libdrm"
RDEPENDS:${PN} = "tegra-libraries-gbm"

COMPATIBLE_MACHINE = "(tegra)"

SRC_REPO = "github.com/oe4t/tegra-udrm-gbm.git;protocol=https"
SRCBRANCH = "master"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCREV = "f027d3297589195df1eddcf3011819322d193e8d"

S = "${WORKDIR}/git"

inherit meson pkgconfig

FILES:${PN} += "${libdir}/gbm"
