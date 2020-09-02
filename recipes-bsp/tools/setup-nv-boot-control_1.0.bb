DESCRIPTION = "Script and systemd service to set up the boot control \
configuration file needed for tegra redundant boot support"
LICENSE = "MIT"
HOMEPAGE = "https://github.com/OE4T/meta-tegra"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "\
    file://setup-nv-boot-control.sh.in \
    file://setup-nv-boot-control.service.in \
    file://setup-nv-boot-control.init.in \
"

COMPATIBLE_MACHINE = "(tegra)"

TNSPEC_TARGET ?= "${MACHINE}"
TNSPEC_BOOTDEV ?= "mmcblk0p1"

S = "${WORKDIR}"
B = "${WORKDIR}/build"

inherit systemd update-rc.d

do_compile() {
    sed -e's,@TARGET@,${TNSPEC_TARGET},g' \
        -e's,@BOOTDEV@,${TNSPEC_BOOTDEV},g' \
        -e's,@sysconfdir@,${sysconfdir},g' \
        ${S}/setup-nv-boot-control.sh.in >${B}/setup-nv-boot-control.sh
    sed -e's,@bindir@,${bindir},g' \
        ${S}/setup-nv-boot-control.service.in >${B}/setup-nv-boot-control.service
    sed -e's,@bindir@,${bindir},g' \
        ${S}/setup-nv-boot-control.init.in >${B}/setup-nv-boot-control.init
}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${B}/setup-nv-boot-control.sh ${D}${bindir}/setup-nv-boot-control
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${B}/setup-nv-boot-control.service ${D}${systemd_system_unitdir}/
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${B}/setup-nv-boot-control.init ${D}${sysconfdir}/init.d/setup-nv-boot-control
}

PACKAGES =+ "${PN}-service"
INITSCRIPT_PACKAGES = "${PN}-service"
INITSCRIPT_NAME = "setup-nv-boot-control"
INITSCRIPT_PARAMS = "defaults 12"
SYSTEMD_PACKAGES = "${PN}-service"
SYSTEMD_SERVICE_${PN}-service = "setup-nv-boot-control.service"
RDEPENDS_${PN}-service = "${PN}"
RDEPENDS_${PN} = "tegra-nv-boot-control-config tegra-eeprom-tool-boardspec"

FILES_${PN} = "${bindir}/setup-nv-boot-control"
FILES_${PN}-service = "${sysconfdir} ${systemd_system_unitdir}"

PACKAGE_ARCH = "${MACHINE_ARCH}"
