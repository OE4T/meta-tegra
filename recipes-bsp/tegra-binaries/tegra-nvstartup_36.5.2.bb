DESCRIPTION = "nvstartup initscript/service"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "\
    file://nvstartup.init \
    file://nvstartup.service \
"

INHIBIT_DEFAULT_DEPS = "1"
COMPATIBLE_MACHINE = "(tegra)"

S = "${WORKDIR}"

do_install() {
    install -d ${D}${sysconfdir}/init.d ${D}${systemd_system_unitdir}
    install -m 0755 ${S}/nvstartup.init ${D}${sysconfdir}/init.d/nvstartup
    install -m 0644 ${S}/nvstartup.service ${D}${systemd_system_unitdir}/
}

inherit systemd update-rc.d

INITSCRIPT_NAME = "nvstartup"
INITSCRIPT_PARAMS = "defaults 00"
SYSTEMD_SERVICE:${PN} = "nvstartup.service"
RDEPENDS:${PN} = "tegra-configs-nvstartup"
