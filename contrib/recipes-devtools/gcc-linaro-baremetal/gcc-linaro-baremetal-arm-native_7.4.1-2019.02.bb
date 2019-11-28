DESCRIPTION = "Linaro baremetal ARM toolchain (prebuilt)"
HOMEPAGE = "https://linaro.org/downloads"
LICENSE = "GPL-3.0-with-GCC-exception & LGPLv3 & GPLv3 & GFDL-1.3"
SECTION = "devel"

BASEVER = "${@'.'.join(d.getVar('PV').split('.')[0:2]) + '-' + d.getVar('PV').split('-')[1]}"
SRC_URI = "https://releases.linaro.org/components/toolchain/binaries/${BASEVER}/arm-eabi/gcc-linaro-${PV}-x86_64_arm-eabi.tar.xz"
SRC_URI[md5sum] = "0a8e5b7b67d713ece5fe24b120393b03"
SRC_URI[sha256sum] = "d4ae43bd325f3a8df2bcfcc1909c1c28356e1e9f5705e21790b17c68830733d7"

LIC_FILES_CHKSUM = "file://share/doc/gcc/Copying.html;md5=4d3dd12c455cd0ef6a53bead7defe99a"

inherit native

S = "${WORKDIR}/gcc-linaro-${PV}-x86_64_arm-eabi"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${datadir}/gcc-linaro-baremetal-arm
    cp -R --no-dereference --preserve=links,mode ${S}/* ${D}${datadir}/gcc-linaro-baremetal-arm/
}

INHIBIT_SYSROOT_STRIP = "1"
