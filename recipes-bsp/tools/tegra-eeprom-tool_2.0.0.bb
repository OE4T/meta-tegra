DESCRIPTION = "Tegra ID EEPROM tools"
HOMEPAGE = "https://github.com/OE4T/tegra-eeprom-tool"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=22c98979a7dd9812d2455ff5dbc88771"

DEPENDS = "libedit"

SRC_URI = "https://github.com/OE4T/${BPN}/releases/download/v${PV}/${BP}.tar.gz"
SRC_URI[sha256sum] = "866d0e8b3b31ce477f8b01c8b3b5e002a082d982c7a2b1d34bcbe258ab563b00"

inherit cmake pkgconfig

RRECOMMENDS:${PN} += "kernel-module-at24"

PACKAGES =+ "${PN}-boardspec"
FILES:${PN}-boardspec = "${bindir}/tegra-boardspec"
