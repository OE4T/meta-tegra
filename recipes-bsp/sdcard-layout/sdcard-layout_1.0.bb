DESCRIPTION = "SDcard layout definition file for Jetson Nano"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://sdcard-layout.in"
INHIBIT_DEFAULT_DEPS = "1"

S = "${WORKDIR}"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    if [ -s "${S}/sdcard-layout.in" ]; then
	install -d ${D}${datadir}/tegraflash
	install -m 0644 ${S}/sdcard-layout.in ${D}${datadir}/tegraflash/
    fi
}

ALLOW_EMPTY_${PN} = "1"
FILES_${PN}-dev = "${datadir}/tegraflash"
PACKAGE_ARCH = "${MACHINE_ARCH}"
