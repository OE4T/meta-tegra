DESCRIPTION = "Tegra ID EEPROM tools"
HOMEPAGE = "https://github.com/OE4T/tegra-eeprom-tool"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=c4bff80c7e9a90aa351e9062b5572544"

DEPENDS = "libedit"

SRC_URI = "https://github.com/OE4T/${BPN}/releases/download/v${PV}/${BP}.tar.gz"
SRC_URI[sha256sum] = "e65452e4abb682063d6b2b37e12596a3e8494bb5325e9fa94e65db078e855c3d"

inherit autotools pkgconfig

RRECOMMENDS_${PN} += "kernel-module-at24"

PACKAGES =+ "${PN}-boardspec"
FILES_${PN}-boardspec = "${bindir}/tegra-boardspec"
