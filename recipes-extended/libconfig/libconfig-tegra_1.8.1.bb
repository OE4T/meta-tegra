SUMMARY = "C/C++ Configuration File Library - tegra-specific build for initrd flashing support"
DESCRIPTION = "Library for manipulating structured configuration files"
HOMEPAGE = "https://hyperrealm.github.io/libconfig/"
BUGTRACKER = "https://github.com/hyperrealm/libconfig/issues"
SECTION = "libs"

LICENSE = "LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING.LIB;md5=17c8e32f0f72580cc2906b409d46b5ac"

SRC_URI = "https://hyperrealm.github.io/libconfig/dist/libconfig-${PV}.tar.gz"
SRC_URI[md5sum] = "82563b674c793dee5175f5d52f202688"
SRC_URI[sha256sum] = "87c6f382994b245f9213be34a2bf19c8ee7d033d7abaa51e88fbb7bad79e2dc6"

PROVIDES = "libconfig"

S = "${UNPACKDIR}/libconfig-${PV}"

inherit autotools-brokensep pkgconfig

EXTRA_OECONF += "--disable-examples"

PACKAGE_BEFORE_PN = "${PN}++"
FILES:${PN}++ = "${libdir}/${BPN}++*${SOLIBS}"

python() {
    if 'openembedded-layer' in d.getVar('BBFILE_COLLECTIONS').split():
        raise bb.parse.SkipRecipe('meta-oe layer present, recipe not required')
}
