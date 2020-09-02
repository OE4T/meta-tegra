DESCRIPTION = "Startup files for Tegra bootloader redundancy support"
LICENSE = "MIT"
HOMEPAGE = "https://github.com/OE4T/meta-tegra"
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

do_install_tegra210() {
    :
}

inherit update-rc.d systemd

ALLOW_EMPTY_${PN} = "1"
INITSCRIPT_PACKAGES = "${PN}"
INITSCRIPT_PACKAGES_tegra210 = ""
INITSCRIPT_NAME = "nv_update_verifier"
SYSTEMD_PACKAGES = "${PN}"
SYSTEMD_PACKAGES_tegra210 = ""
SYSTEMD_SERVICE_${PN} = "nv_update_verifier.service"
RDEPENDS_${PN} = "tegra-redundant-boot-base"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
