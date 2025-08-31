DESCRIPTION = "Scripts and configuration files for setting up the \
 integrated Bluetooth module on Tegra SoMs that have one."
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "\
    file://tegra-bluetooth-init.sh \
    file://tegra-bluetooth.rules \
    file://tegra-bluetooth.service \
"

COMPATIBLE_MACHINE = "tegra"

TEGRA_BT_SUPPORT_PACKAGE ??= "tegra-brcm-patchram"

S = "${UNPACKDIR}"

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

ALLOW_EMPTY:${PN} = "1"
SYSTEMD_SERVICE:${PN} = "${@bb.utils.contains('MACHINE_FEATURES', 'bluetooth', 'tegra-bluetooth.service', '', d)}"
FILES:${PN} += "${nonarch_base_libdir}/udev/rules.d"
RDEPENDS:${PN} = "${@bb.utils.contains('MACHINE_FEATURES', 'bluetooth', '${TEGRA_BT_SUPPORT_PACKAGE}', '', d)} nvidia-kernel-oot-bluetooth"
PACKAGE_ARCH = "${MACHINE_ARCH}"
