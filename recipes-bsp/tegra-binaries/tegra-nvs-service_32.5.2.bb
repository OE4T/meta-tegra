DESCRIPTION = "nvs-service startup files"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "\
    file://nvs-service.init \
    file://nvs-service.service \
"

INHIBIT_DEFAULT_DEPS = "1"
COMPATIBLE_MACHINE = "(tegra)"

S = "${WORKDIR}"

do_install() {
    install -d ${D}${systemd_system_unitdir} ${D}${sysconfdir}/init.d
    install -m 0644 ${S}/nvs-service.service ${D}${systemd_system_unitdir}
    install -m 0755 ${S}/nvs-service.init ${D}${sysconfdir}/init.d/nvs-service
    sed -i -e's,/usr/sbin,${sbindir},g' ${D}${systemd_system_unitdir}/nvs-service.service
}

inherit systemd update-rc.d

INITSCRIPT_NAME = "nvs-service"
INITSCRIPT_PARAMS = "defaults"
SYSTEMD_SERVICE:${PN} = "nvs-service.service"
RDEPENDS:${PN} = "tegra-nvs-base"

PACKAGE_ARCH = "${TEGRA_PKGARCH}"
