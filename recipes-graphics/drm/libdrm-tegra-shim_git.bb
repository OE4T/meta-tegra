DESCRIPTION = "Shim library for L4T libdrm that works under emulation"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d0988ef952643512d96488ffa614a795"

COMPATIBLE_MACHINE = "(tegra186|tegra210)"

SRC_REPO ?= "github.com/madisongh/libdrm-tegra-shim"
SRCBRANCH ?= "master"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCREV ?= "82357b0aecc9614035effca9c340b291cf52293e"

PV = "2.4+git${SRCPV}"

PROVIDES = "libdrm drm"

S = "${WORKDIR}/git"

inherit autotools

PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"

RPROVIDES_${PN} += "libdrm2"
RDEPENDS_${PN} = "libdrm-nvdc"
