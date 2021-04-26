DESCRIPTION = "Adds a modprobe config for tegra_udrm"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://tegra-udrm.conf"

COMPATIBLE_MACHINE = "(tegra)"

do_install() {
    install -d ${D}${sysconfdir}/modprobe.d
    install -m 0644 ${WORKDIR}/tegra-udrm.conf ${D}${sysconfdir}/modprobe.d/
}
