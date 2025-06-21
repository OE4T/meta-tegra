SUMMARY = "C/C++ Configuration File Library - tegra-specific build for initrd flashing support"
DESCRIPTION = "Library for manipulating structured configuration files"
HOMEPAGE = "https://hyperrealm.github.io/libconfig/"
BUGTRACKER = "https://github.com/hyperrealm/libconfig/issues"
SECTION = "libs"

LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=fad9b3332be894bab9bc501572864b29"

SRC_URI = "https://hyperrealm.github.io/libconfig/dist/libconfig-${PV}.tar.gz"
SRC_URI[md5sum] = "15ec701205f91f21b1187f8b61e0d64f"
SRC_URI[sha256sum] = "545166d6cac037744381d1e9cc5a5405094e7bfad16a411699bcff40bbb31ee7"

PROVIDES = "libconfig"

S = "${UNPACKDIR}/libconfig-${PV}"

inherit autotools-brokensep pkgconfig

PACKAGE_BEFORE_PN = "${PN}++"
FILES:${PN}++ = "${libdir}/${BPN}++*${SOLIBS}"

python() {
    if 'openembedded-layer' in d.getVar('BBFILE_COLLECTIONS').split():
        raise bb.parse.SkipRecipe('meta-oe layer present, recipe not required')
}
