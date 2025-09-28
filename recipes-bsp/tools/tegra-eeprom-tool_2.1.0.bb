DESCRIPTION = "Tegra ID EEPROM tools"
HOMEPAGE = "https://github.com/OE4T/tegra-eeprom-tool"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=8a8a812fed70c7ff54c110c71eb80207"

DEPENDS = "libedit"

SRC_URI = "https://github.com/OE4T/${BPN}/releases/download/v${PV}/${BP}.tar.gz"
SRC_URI[sha256sum] = "8d729064693cee7f7a7c9242bc75acda6ac2d64dc8ccfbac0408ee76744abfb0"

inherit cmake pkgconfig

RRECOMMENDS:${PN} += "kernel-module-at24"

PACKAGES =+ "${PN}-boardspec"
FILES:${PN}-boardspec = "${bindir}/tegra-boardspec"
