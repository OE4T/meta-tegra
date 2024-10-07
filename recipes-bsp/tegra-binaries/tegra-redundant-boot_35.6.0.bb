DESCRIPTION = "Startup files for Tegra bootloader redundancy support"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "\
    file://nv_update_verifier.init \
    file://nv_update_verifier.service \
"

INHIBIT_DEFAULT_DEPS = "1"
COMPATIBLE_MACHINE = "(tegra)"
PR = "r1"

S = "${WORKDIR}"

do_install() {
    install -d ${D}${sysconfdir}/init.d
    install -m 0755 ${S}/nv_update_verifier.init ${D}${sysconfdir}/init.d/nv_update_verifier
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${S}/nv_update_verifier.service ${D}${systemd_system_unitdir}
}

inherit update-rc.d systemd

ALLOW_EMPTY:${PN} = "1"
INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_NAME = "nv_update_verifier"
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_SERVICE:${PN} = "nv_update_verifier.service"
RDEPENDS:${PN} = "tegra-redundant-boot-base"
PACKAGE_ARCH = "${L4T_BSP_PKGARCH}"
