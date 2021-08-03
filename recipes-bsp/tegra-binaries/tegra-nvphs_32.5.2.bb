DESCRIPTION = "nvphs startup files"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "\
    file://nvphs.init \
    file://nvphs.service \
"

INHIBIT_DEFAULT_DEPS = "1"
COMPATIBLE_MACHINE = "(tegra)"

S = "${WORKDIR}"

do_install() {
    install -d ${D}${systemd_system_unitdir} ${D}${sysconfdir}/init.d
    install -m 0644 ${S}/nvphs.service ${D}${systemd_system_unitdir}
    install -m 0755 ${S}/nvphs.init ${D}${sysconfdir}/init.d/nvphs
    sed -i -e's,/usr/sbin,${sbindir},g' ${D}${systemd_system_unitdir}/nvphs.service
}

inherit systemd update-rc.d

INITSCRIPT_NAME = "nvphs"
INITSCRIPT_PARAMS = "defaults"
SYSTEMD_SERVICE:${PN} = "nvphs.service"

RDEPENDS:${PN} = "tegra-nvphs-base"
