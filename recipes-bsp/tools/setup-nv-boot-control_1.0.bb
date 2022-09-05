DESCRIPTION = "Script and systemd service to set up the boot control \
configuration file needed for tegra redundant boot support"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "\
    file://setup-nv-boot-control.sh.in \
    file://setup-nv-boot-control.service.in \
    file://setup-nv-boot-control.init.in \
    file://esp.mount.in \
"

COMPATIBLE_MACHINE = "(tegra)"

ESPMOUNT ?= "/boot/efi"
NVIDIA_ESPMOUNT ?= "/opt/nvidia/esp"
ESPMOUNTUNIT ?= "${@'-'.join(d.getVar('ESPMOUNT').split('/')[1:])}.mount"
ESPVARDIR ?= "${ESPMOUNT}/EFI/NVDA/Variables"

S = "${WORKDIR}"
B = "${WORKDIR}/build"

inherit systemd update-rc.d

do_compile() {
    sed -e's,@TARGET@,${TNSPEC_MACHINE},g' \
        -e's,@BOOTDEV@,${TNSPEC_BOOTDEV},g' \
        -e's,@ESPMOUNT@,${ESPMOUNT},g' \
        -e's,@NVIDIA_ESPMOUNT@,${NVIDIA_ESPMOUNT},g' \
        -e's,@ESPVARDIR@,${ESPVARDIR},g' \
        -e's,@sysconfdir@,${sysconfdir},g' \
        ${S}/setup-nv-boot-control.sh.in >${B}/setup-nv-boot-control.sh
    sed -e's,@bindir@,${bindir},g' \
        -e's,@ESPMOUNT@,${ESPMOUNT},g' \
        ${S}/setup-nv-boot-control.service.in >${B}/setup-nv-boot-control.service
    sed -e's,@bindir@,${bindir},g' \
        -e's,@ESPMOUNT@,${ESPMOUNT},g' \
        ${S}/setup-nv-boot-control.init.in >${B}/setup-nv-boot-control.init
    sed -e's,@ESPMOUNT@,${ESPMOUNT},g' \
        ${S}/esp.mount.in >${B}/${ESPMOUNTUNIT}

}

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${B}/setup-nv-boot-control.sh ${D}${bindir}/setup-nv-boot-control
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${B}/setup-nv-boot-control.service ${D}${systemd_system_unitdir}/
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${B}/setup-nv-boot-control.init ${D}${sysconfdir}/init.d/setup-nv-boot-control
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${B}/${ESPMOUNTUNIT} ${D}${systemd_system_unitdir}/
    install -d ${D}${ESPMOUNT} ${D}${NVIDIA_ESPMOUNT}
}

pkg_postinst:${PN}() {
    if [ ! -d $D${systemd_system_unitdir}/local-fs.target.wants ]; then
        mkdir -p $D${systemd_system_unitdir}/local-fs.target.wants
    fi
    ln -sf ../${ESPMOUNTUNIT} $D${systemd_system_unitdir}/local-fs.target.wants/
}

PACKAGES =+ "${PN}-service"
INITSCRIPT_PACKAGES = "${PN}-service"
INITSCRIPT_NAME = "setup-nv-boot-control"
INITSCRIPT_PARAMS = "defaults 12"
SYSTEMD_PACKAGES = "${PN}-service"
SYSTEMD_SERVICE:${PN}-service = "setup-nv-boot-control.service"
RDEPENDS:${PN}-service = "${PN}"
RDEPENDS:${PN} = "efivar tegra-nv-boot-control-config tegra-eeprom-tool-boardspec"

FILES:${PN} = "${bindir}/setup-nv-boot-control /opt/nvidia ${ESPMOUNT}"
FILES:${PN}-service = "${sysconfdir} ${systemd_system_unitdir}"

PACKAGE_ARCH = "${MACHINE_ARCH}"
