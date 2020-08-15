DESCRIPTION = "Tegra ID EEPROM tools"
HOMEPAGE = "https://github.com/OE4T/tegra-eeprom-tool"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c4bff80c7e9a90aa351e9062b5572544"

DEPENDS = "libedit"

SRC_REPO ?= "github.com/OE4T/tegra-eeprom-tool"
SRCBRANCH ?= "master"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCREV = "${AUTOREV}"
PV = "1.1+git${SRCPV}"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

PACKAGES =+ "${PN}-boardspec"
FILES_${PN}-boardspec = "${bindir}/tegra-boardspec"
