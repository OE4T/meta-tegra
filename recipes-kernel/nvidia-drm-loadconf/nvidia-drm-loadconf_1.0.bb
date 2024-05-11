DESCRIPTION = "Adds a modprobe and modules load config for nvidia drm"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "\
    file://nvidia-drm-modprobe.conf \
"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"

COMPATIBLE_MACHINE = "(tegra234)"

do_install() {
    install -d ${D}${sysconfdir}/modprobe.d ${D}${sysconfdir}/modules-load.d
    install -m 0644 ${UNPACKDIR}/nvidia-drm-modprobe.conf ${D}${sysconfdir}/modprobe.d/nvidia-drm.conf
    echo "nvidia-drm" > ${D}${sysconfdir}/modules-load.d/nvidia-drm.conf
}
