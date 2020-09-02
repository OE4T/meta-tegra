DESCRIPTION = "Scripts and configuration files for setting up the \
 integrated Bluetooth module on Tegra SoMs that have one."
LICENSE = "MIT"
HOMEPAGE = "https://github.com/OE4T/meta-tegra"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "\
    file://tegra-bluetooth-init.sh \
    file://tegra-bluetooth.rules \
    file://tegra-bluetooth.service \
"

COMPATIBLE_MACHINE = "tegra"

S = "${WORKDIR}"

inherit systemd

do_install() {
    if ${@bb.utils.contains('MACHINE_FEATURES', 'bluetooth', 'true', 'false', d)}; then
        install -d ${D}${sbindir}
	install -m 0755 ${S}/tegra-bluetooth-init.sh ${D}${sbindir}/tegra-bluetooth-init
	install -d ${D}${systemd_system_unitdir}
	install -m 0644 ${S}/tegra-bluetooth.service ${D}${systemd_system_unitdir}/
	install -d ${D}${nonarch_base_libdir}/udev/rules.d
	install -m 0644 ${S}/tegra-bluetooth.rules ${D}${nonarch_base_libdir}/udev/rules.d/99-tegra-bluetooth.rules
    fi
}

ALLOW_EMPTY_${PN} = "1"
SYSTEMD_SERVICE_${PN} = "${@bb.utils.contains('MACHINE_FEATURES', 'bluetooth', 'tegra-bluetooth.service', '', d)}"
FILES_${PN} += "${nonarch_base_libdir}/udev/rules.d"
RDEPENDS_${PN} = "${@bb.utils.contains('MACHINE_FEATURES', 'bluetooth', 'tegra-brcm-patchram', '', d)}"
RRECOMMENDS_${PN} = "kernel-module-bluedroid-pm"
PACKAGE_ARCH = "${MACHINE_ARCH}"
