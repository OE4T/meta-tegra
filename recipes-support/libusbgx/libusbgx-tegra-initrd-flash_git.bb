SUMMARY = "USB Gadget neXt Configfs Library build for Tegra initrd flashing"
LICENSE = "GPL-2.0-only & LGPL-2.1-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=b234ee4d69f5fce4486a80fdaf4a4263 \
                    file://COPYING.LGPL;md5=4fbd65380cdd255951079008b364516c"

inherit autotools pkgconfig

COMPATIBLE_MACHINE = "(tegra)"

PV = "0.2.0+git${SRCPV}"
SRCREV = "45c14ef4d5d7ced0fbf984208de44ced6d5ed898"
SRCBRANCH = "master"
SRC_REPO = "github.com/libusbgx/libusbgx.git;protocol=https"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"

S = "${WORKDIR}/git"

PACKAGECONFIG = "examples gadget-schemes libconfig"
PACKAGECONFIG[libconfig] = "--with-libconfig=yes,--without-libconfig,libconfig-tegra"
PACKAGECONFIG[examples] = "--enable-examples,--disable-examples"
PACKAGECONFIG[gadget-schemes] = "--enable-gadget-schemes,--disable-gadget-schemes"
PACKAGECONFIG[tests] = "--enable-tests,--disable-tests,cmocka"

RCONFLICTS:${PN} = "libusbgx libusbgx-examples"
PACKAGE_ARCH = "${TEGRA_PKGARCH}"
