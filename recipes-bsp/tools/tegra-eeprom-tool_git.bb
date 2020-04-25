DESCRIPTION = "Tegra ID EEPROM tools"
HOMEPAGE = "https://github.com/madisongh/tegra-eeprom-tool"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c4bff80c7e9a90aa351e9062b5572544"

DEPENDS = "libedit"

SRC_REPO ?= "github.com/madisongh/tegra-eeprom-tool"
SRCBRANCH ?= "master"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCREV = "39f3569d128b80efd1ab12c040639f506f35ae9c"
PV = "1.1+git${SRCPV}"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

PACKAGES =+ "${PN}-boardspec"
FILES_${PN}-boardspec = "${bindir}/tegra-boardspec"
