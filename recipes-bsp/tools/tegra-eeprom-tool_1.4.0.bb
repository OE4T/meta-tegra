DESCRIPTION = "Tegra ID EEPROM tools"
HOMEPAGE = "https://github.com/OE4T/tegra-eeprom-tool"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=22c98979a7dd9812d2455ff5dbc88771"

DEPENDS = "libedit"

SRC_URI = "https://github.com/OE4T/${BPN}/releases/download/v${PV}/${BP}.tar.gz"
SRC_URI[sha256sum] = "dea28aabff2073aab8dd04a73880e4d5e47f6e7ae9e896a27e38686bd9ceb8f4"

inherit cmake pkgconfig

RRECOMMENDS:${PN} += "kernel-module-at24"

PACKAGES =+ "${PN}-boardspec"
FILES:${PN}-boardspec = "${bindir}/tegra-boardspec"
