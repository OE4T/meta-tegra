DESCRIPTION = "Tegra ID EEPROM tools"
HOMEPAGE = "https://github.com/OE4T/tegra-eeprom-tool"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=22c98979a7dd9812d2455ff5dbc88771"

DEPENDS = "libedit"

SRC_URI = "https://github.com/OE4T/${BPN}/releases/download/v${PV}/${BP}.tar.gz"
SRC_URI[sha256sum] = "56e8e3d73a23d919e80b5398903f68e60dda71c4f9ca289cecf043bb496989f3"

inherit cmake pkgconfig

RRECOMMENDS:${PN} += "kernel-module-at24"

PACKAGES =+ "${PN}-boardspec"
FILES:${PN}-boardspec = "${bindir}/tegra-boardspec"
