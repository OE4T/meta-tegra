DESCRIPTION = "nvargusdaemon initscript/service"
LICENSE = "MIT"
HOMEPAGE = "https://github.com/OE4T/meta-tegra"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "\
    file://nvargus-daemon.init \
    file://nvargus-daemon.service \
"

INHIBIT_DEFAULT_DEPS = "1"
COMPATIBLE_MACHINE = "(tegra)"

S = "${WORKDIR}"

do_install() {
    install -d ${D}${sysconfdir}/init.d
    install -m755 ${S}/nvargus-daemon.init ${D}${sysconfdir}/init.d/nvargus-daemon
    install -d ${D}${systemd_system_unitdir}
    install -m644 ${S}/nvargus-daemon.service ${D}${systemd_system_unitdir}
}

inherit systemd update-rc.d

INITSCRIPT_NAME = "nvargus-daemon"
INITSCRIPT_PARAMS = "defaults"
SYSTEMD_SERVICE_${PN} = "nvargus-daemon.service"
RDEPENDS_${PN} = "tegra-libraries-argus-daemon-base"
