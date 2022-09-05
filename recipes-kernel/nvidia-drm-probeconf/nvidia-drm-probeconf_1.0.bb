DESCRIPTION = "Adds a modprobe config for nvidia drm"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://nvidia-drm.conf"

COMPATIBLE_MACHINE = "(tegra234)"

do_install() {
    install -d ${D}${sysconfdir}/modprobe.d
    install -m 0644 ${WORKDIR}/nvidia-drm.conf ${D}${sysconfdir}/modprobe.d/
}
