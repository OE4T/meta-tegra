DESCRIPTION = "Script and systemd service to set up the boot control \
configuration file needed for tegra redundant boot support"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "\
    file://setup-nv-boot-control.sh.in \
    file://setup-nv-boot-control.service.in \
    file://setup-nv-boot-control.init.in \
    file://esp.mount.in \
    file://uefi_common.func.in \
    file://oe4t-set-uefi-OSIndications \
"

COMPATIBLE_MACHINE = "(tegra)"

ESPMOUNT ?= "/boot/efi"
ESPMOUNTUNIT ?= "${@'-'.join(d.getVar('ESPMOUNT').split('/')[1:])}.mount"

S = "${WORKDIR}/sources"
UNPACKDIR = "${S}"
B = "${WORKDIR}/build"

inherit systemd update-rc.d

do_compile() {
    sed -e's,@TARGET@,${TNSPEC_MACHINE},g' \
        -e's,@BOOTDEV@,${TNSPEC_BOOTDEV},g' \
        -e's,@sysconfdir@,${sysconfdir},g' \
        ${S}/setup-nv-boot-control.sh.in >${B}/setup-nv-boot-control.sh
    cp ${S}/uefi_common.func.in ${B}/uefi_common.func
    sed -e's,@bindir@,${bindir},g' \
        ${S}/setup-nv-boot-control.service.in >${B}/setup-nv-boot-control.service
    sed -e's,@bindir@,${bindir},g' \
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
    install -d ${D}${ESPMOUNT}
    install -m 0755 ${B}/uefi_common.func ${D}${bindir}/
    install -m 0755 ${S}/oe4t-set-uefi-OSIndications ${D}${bindir}/
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

FILES:${PN} = "\
    ${bindir}/setup-nv-boot-control \
    /opt/nvidia ${ESPMOUNT} \
    ${bindir}/uefi_common.func \
    ${bindir}/oe4t-set-uefi-OSIndications \
"
FILES:${PN}-service = "${sysconfdir} ${systemd_system_unitdir}"

PACKAGE_ARCH = "${MACHINE_ARCH}"
