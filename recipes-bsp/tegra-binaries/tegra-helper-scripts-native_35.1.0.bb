DESCRIPTION = "Flash helper scripts"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

INHIBIT_DEFAULT_DEPS = "1"

inherit native

SRC_URI = " \
    file://tegra194-flash-helper.sh \
    file://tegra234-flash-helper.sh \
    file://nvflashxmlparse.py \
    file://make-sdcard.sh \
    file://tegra-signimage-helper.sh \
    file://find-jetson-usb.sh \
    file://rewrite-tegraflash-args.py \
    file://initrd-flash.sh \
    file://brcid-to-uid.py \
"

S = "${WORKDIR}"

do_install() {
    install -d ${D}${bindir}/tegra-flash
    install -m 0755 ${S}/tegra194-flash-helper.sh ${D}${bindir}/tegra-flash/
    install -m 0755 ${S}/tegra234-flash-helper.sh ${D}${bindir}/tegra-flash/
    install -m 0755 ${S}/nvflashxmlparse.py ${D}${bindir}/tegra-flash/nvflashxmlparse
    install -m 0755 ${S}/make-sdcard.sh ${D}${bindir}/tegra-flash/make-sdcard
    install -m 0755 ${S}/tegra-signimage-helper.sh ${D}${bindir}/tegra-flash/tegra-signimage-helper
    install -m 0755 ${S}/find-jetson-usb.sh ${D}${bindir}/tegra-flash/find-jetson-usb
    install -m 0755 ${S}/rewrite-tegraflash-args.py ${D}${bindir}/tegra-flash/rewrite-tegraflash-args
    install -m 0755 ${S}/initrd-flash.sh ${D}${bindir}/tegra-flash/initrd-flash
    install -m 0755 ${S}/brcid-to-uid.py ${D}${bindir}/tegra-flash/brcid-to-uid
}
