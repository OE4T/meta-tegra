DESCRIPTION = "Flash helper scripts"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

INHIBIT_DEFAULT_DEPS = "1"

inherit_defer native

SRC_URI = " \
    file://tegra-flash-helper.sh \
    file://nvflashxmlparse.py \
    file://nvbct-config.py \
    file://make-sdcard.sh \
    file://find-jetson-usb.sh \
    file://initrd-flash.sh \
"

S = "${UNPACKDIR}"

do_install() {
    install -d ${D}${bindir}/tegra-flash
    install -m 0755 ${S}/tegra-flash-helper.sh ${D}${bindir}/tegra-flash/
    install -m 0755 ${S}/nvflashxmlparse.py ${D}${bindir}/tegra-flash/nvflashxmlparse
    install -m 0755 ${S}/nvbct-config.py ${D}${bindir}/tegra-flash/nvbct-config
    install -m 0755 ${S}/make-sdcard.sh ${D}${bindir}/tegra-flash/make-sdcard
    install -m 0755 ${S}/find-jetson-usb.sh ${D}${bindir}/tegra-flash/find-jetson-usb
    install -m 0755 ${S}/initrd-flash.sh ${D}${bindir}/tegra-flash/initrd-flash
}
