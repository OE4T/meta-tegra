DESCRIPTION = "Script and systemd service to set up the boot control \
configuration file needed for tegra redundant boot support"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "\
    file://setup-nv-boot-control.sh.in \
    file://setup-nv-boot-control.service.in \
"

COMPATIBLE_MACHINE = "(tegra)"

TNSPEC_TARGET ?= "${MACHINE}"
TNSPEC_BOOTDEV ?= "mmcblk0p1"

S = "${WORKDIR}"
B = "${WORKDIR}/build"

inherit systemd

do_compile() {
    sed -e's,@TARGET@,${TNSPEC_TARGET},g' \
        -e's,@BOOTDEV@,${TNSPEC_BOOTDEV},g' \
        -e's,@sysconfdir@,${sysconfdir},g' \
        ${S}/setup-nv-boot-control.sh.in >${B}/setup-nv-boot-control.sh
    sed -e's,@bindir@,${bindir},g' \
        ${S}/setup-nv-boot-control.service.in >${B}/setup-nv-boot-control.service
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${B}/setup-nv-boot-control.sh ${D}${bindir}/setup-nv-boot-control
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${B}/setup-nv-boot-control.service ${D}${systemd_system_unitdir}/
}

PACKAGES =+ "${PN}-service"
SYSTEMD_PACKAGES = "${PN}-service"
SYSTEMD_SERVICE_${PN}-service = "setup-nv-boot-control.service"
RDEPENDS_${PN}-service = "${PN}"
RDEPENDS_${PN} = "tegra-nv-boot-control-config tegra-eeprom-tool-boardspec"

PACKAGE_ARCH = "${MACHINE_ARCH}"
