DESCRIPTION = "Replacement modules for CMake to fix issues with FindCUDA"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=9cdfdd8bf426655841e437d0343463d6"

SRC_URI = "https://github.com/OE4T/${BPN}/releases/download/v${PV}/${BP}.tar.gz"
SRC_URI[sha256sum] = "9b9a416ccb669a7c9dc75df0631b0166a016c3f16e99bda70018f84b4368fca2"

DEPENDS = "cmake-native"

do_configure() {
    ./configure
    [ -e Makefile ] || bberror "configure script failed"
}

do_install() {
    oe_runmake install DESTDIR="${D}"
}

FILES:${PN}-dev += "${datadir}/cmake"
ALLOW_EMPTY:${PN} = "1"
