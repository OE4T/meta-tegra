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
SRCREV = "26911c2f1625f124942d553765f386c284f2d7ab"

S = "${WORKDIR}/git"

inherit meson pkgconfig

FILES:${PN} += "${libdir}/gbm"
