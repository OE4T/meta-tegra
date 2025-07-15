DESCRIPTION = "nvsciipc startup files"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "\
    file://nv_nvsciipc_init.init \
    file://nv_nvsciipc_init.service \
    file://61-nvsciipc.rules \
"

INHIBIT_DEFAULT_DEPS = "1"
COMPATIBLE_MACHINE = "(tegra)"

S = "${UNPACKDIR}"

do_install() {
    install -d ${D}${systemd_system_unitdir} ${D}${sysconfdir}/init.d
    install -m 0644 ${S}/nv_nvsciipc_init.service ${D}${systemd_system_unitdir}
    install -m 0755 ${S}/nv_nvsciipc_init.init ${D}${sysconfdir}/init.d/nv_nvsciipc_init
    sed -i -e's,/usr/bin,${bindir},g' ${D}${systemd_system_unitdir}/nv_nvsciipc_init.service
    install -d ${D}${sysconfdir}/udev/rules.d
    install -m 0644 ${S}/61-nvsciipc.rules ${D}${sysconfdir}/udev/rules.d/
}

inherit systemd update-rc.d

INITSCRIPT_NAME = "nv_nvsciipc_init"
INITSCRIPT_PARAMS = "defaults"
SYSTEMD_SERVICE:${PN} = "nv_nvsciipc_init.service"

RDEPENDS:${PN} = "tegra-nvsciipc-base"
