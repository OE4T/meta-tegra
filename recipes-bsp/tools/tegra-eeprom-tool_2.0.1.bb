DESCRIPTION = "Tegra ID EEPROM tools"
HOMEPAGE = "https://github.com/OE4T/tegra-eeprom-tool"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=22c98979a7dd9812d2455ff5dbc88771"

DEPENDS = "libedit"

SRC_URI = "https://github.com/OE4T/${BPN}/releases/download/v${PV}/${BP}.tar.gz"
SRC_URI[sha256sum] = "53a5b69d6a4f8835192622c6dbcfa10af562fb198b5ad7e46e7aac2b73fa2248"

inherit cmake pkgconfig

RRECOMMENDS:${PN} += "kernel-module-at24"

PACKAGES =+ "${PN}-boardspec"
FILES:${PN}-boardspec = "${bindir}/tegra-boardspec"
