DESCRIPTION = "Flash helper scripts"
LICENSE = "MIT"
HOMEPAGE = "https://github.com/OE4T/meta-tegra"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

INHIBIT_DEFAULT_DEPS = "1"

inherit native

SRC_URI = " \
    file://tegra186-flash-helper.sh \
    file://tegra194-flash-helper.sh \
    file://tegra210-flash-helper.sh \
    file://nvflashxmlparse.py \
    file://make-sdcard.sh \
"

S = "${WORKDIR}"

do_install() {
    install -d ${D}${bindir}/tegra186-flash ${D}${bindir}/tegra210-flash
    install -m 0755 ${S}/tegra186-flash-helper.sh ${D}${bindir}/tegra186-flash/
    install -m 0755 ${S}/tegra194-flash-helper.sh ${D}${bindir}/tegra186-flash/
    install -m 0755 ${S}/tegra210-flash-helper.sh ${D}${bindir}/tegra210-flash/
    install -m 0755 ${S}/nvflashxmlparse.py ${D}${bindir}/tegra210-flash/nvflashxmlparse
    install -m 0755 ${S}/make-sdcard.sh ${D}${bindir}/tegra210-flash/make-sdcard
}
