DESCRIPTION = "Replacement modules for CMake to fix issues with FindCUDA"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d171e3bbe1251b3bc6c85aa9c5bf36f5"

SRC_URI = "https://github.com/madisongh/${BPN}/releases/download/v${PV}/${BP}.tar.gz"
SRC_URI[sha256sum] = "0d87debfa1050a2867143e2bf32cd824e90aec1ca2f9690d27b261da7a8134de"

DEPENDS = "cmake-native"

do_configure() {
    ./configure
    [ -e Makefile ] || bberror "configure script failed"
}

do_install() {
    oe_runmake install DESTDIR="${D}"
}

FILES_${PN}-dev += "${datadir}/cmake"
ALLOW_EMPTY_${PN} = "1"
