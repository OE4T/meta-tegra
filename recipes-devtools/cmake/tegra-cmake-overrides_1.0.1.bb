DESCRIPTION = "Replacement modules for CMake to fix issues with FindCUDA"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://LICENSE;md5=d171e3bbe1251b3bc6c85aa9c5bf36f5"

SRC_URI = "https://github.com/madisongh/${BPN}/releases/download/v${PV}/${BP}.tar.gz"
SRC_URI[sha256sum] = "fd8e1da492fb1a3124525c52e789d3c70a310f4dfee7b3d18372e3db72ca7ad2"

do_install() {
    oe_runmake install DESTDIR="${D}"
}

FILES_${PN}-dev += "${datadir}/cmake"
ALLOW_EMPTY_${PN} = "1"
