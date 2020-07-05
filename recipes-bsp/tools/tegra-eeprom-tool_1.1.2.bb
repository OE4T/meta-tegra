DESCRIPTION = "Tegra ID EEPROM tools"
HOMEPAGE = "https://github.com/madisongh/tegra-eeprom-tool"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c4bff80c7e9a90aa351e9062b5572544"

DEPENDS = "libedit"

SRC_URI = "https://github.com/madisongh/${BPN}/releases/download/v${PV}/${BP}.tar.gz"
SRC_URI[sha256sum] = "18002a9a09262b2167d97b4c77ec3ed304f6244383a36b63ba20c1503b2acd12"

inherit autotools pkgconfig

PACKAGES =+ "${PN}-boardspec"
FILES_${PN}-boardspec = "${bindir}/tegra-boardspec"
