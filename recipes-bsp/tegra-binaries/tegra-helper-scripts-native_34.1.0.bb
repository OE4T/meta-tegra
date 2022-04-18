DESCRIPTION = "Flash helper scripts"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

INHIBIT_DEFAULT_DEPS = "1"

inherit native

SRC_URI = " \
    file://${SOC_FAMILY}-flash-helper.sh \
    file://nvflashxmlparse.py \
    file://make-sdcard.sh \
    file://tegra-signimage-helper.sh \
"

S = "${WORKDIR}"

do_install() {
    install -d ${D}${bindir}/${SOC_FAMILY}-flash
    install -m 0755 ${S}/${SOC_FAMILY}-flash-helper.sh ${D}${bindir}/${SOC_FAMILY}-flash/
    install -m 0755 ${S}/nvflashxmlparse.py ${D}${bindir}/${SOC_FAMILY}-flash/nvflashxmlparse
    install -m 0755 ${S}/make-sdcard.sh ${D}${bindir}/${SOC_FAMILY}-flash/make-sdcard
    install -m 0755 ${S}/tegra-signimage-helper.sh ${D}${bindir}/${SOC_FAMILY}-flash/tegra-signimage-helper
}
