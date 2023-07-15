DESCRIPTION = "nvpmodel startup files"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "\
    file://nvpmodel.init \
    file://nvpmodel.service \
"

INHIBIT_DEFAULT_DEPS = "1"
COMPATIBLE_MACHINE = "(tegra)"

S = "${WORKDIR}"

do_install() {
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${S}/nvpmodel.init ${D}${sysconfdir}/init.d/nvpmodel
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/nvpmodel.service ${D}${systemd_system_unitdir}
}

inherit systemd update-rc.d

INITSCRIPT_NAME = "nvpmodel"
INITSCRIPT_PARAMS = "defaults"
SYSTEMD_SERVICE:${PN} = "nvpmodel.service"
RDEPENDS:${PN} = "tegra-nvpmodel-base tegra-nvpower"
