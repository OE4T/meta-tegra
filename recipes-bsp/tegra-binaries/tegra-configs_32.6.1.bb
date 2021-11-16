DESCRIPTION = "Miscellaneous configuration files provided by L4T"
L4T_DEB_COPYRIGHT_MD5 = "5e719af08f160720e26b2a78670d44bd"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-configs"
TEGRA_LIBRARIES_CONTAINER_CSV = ""

require tegra-debian-libraries-common.inc

# We're using the current version for PV, but the base -configs
# package is still the original, so set SRC_SOC_DEBS accordingly
SRC_SOC_DEBS = "\
    ${L4T_DEB_TRANSLATED_BPN}_${L4T_VERSION}-${L4T_BSP_DEB_ORIG_VERSION}_arm64.deb;subdir=${BP};name=main \
    nvidia-l4t-init_${PV}_arm64.deb;subdir=${BP};name=init \
    nvidia-l4t-x11_${PV}_arm64.deb;subdir=${BP};name=x11 \
"

MAINSUM = "20c27ceff1d8f75df8715236f5712b5d9dbe6684c6550e777ffc37455a2cd3c5"
MAINSUM_tegra210 = "29fa498e9aeb0341c9baa08c75160a7343f51e1465357f7c7aa5fc84a88a8a14"
INITSUM = "97ecfa2e2a1bd9dd7537c1e272ce3b44dc56b859f1b5b0a8131549872669a93b"
INITSUM_tegra210 = "bee82489940152358039b5b7123815620447415121f03843f711a8f4158b23b4"
X11SUM = "c323636b082d437a021fd70f42e5a3579d89b9750bd7ce61fdf1b70ec434e752"
X11SUM_tegra210 = "2f450a5d042911241897826a4f6c79644ab4d7f435b8d0d9f79bfcffa345de54"
SRC_URI[init.sha256sum] = "${INITSUM}"
SRC_URI[x11.sha256sum] = "${X11SUM}"

SRC_URI += "\
    file://0001-Patch-udev-rules-for-OE-use.patch \
    file://0002-Patch-nv.sh-script-for-OE-use.patch \
"

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${S}/etc/systemd/nv.sh ${D}${sbindir}/nvstartup
    install -d ${D}/${sysconfdir}/udev/rules.d
    install -m 0644 ${S}/etc/udev/rules.d/99-tegra-devices.rules ${D}${sysconfdir}/udev/rules.d
    install -m 0644 ${S}/etc/udev/rules.d/99-tegra-mmc-ra.rules ${D}${sysconfdir}/udev/rules.d
    install -m 0644 ${S}/etc/udev/rules.d/99-nv-l4t-usb-host-config.rules ${D}${sysconfdir}/udev/rules.d

    install -d ${D}${sysconfdir}/X11

    install -m 0644 ${S}/etc/enctune.conf ${D}${sysconfdir}
}

do_install_append_tegra186() {
    install -m 0644 ${S}/etc/X11/xorg.conf.t186_ref ${D}${sysconfdir}/X11/xorg.conf
}
do_install_append_tegra194() {
    install -m 0644 ${S}/etc/X11/xorg.conf.t194_ref ${D}${sysconfdir}/X11/xorg.conf
}
do_install_append_tegra210() {
    install -m 0644 ${S}/etc/X11/xorg.conf ${D}${sysconfdir}/X11/
}

PACKAGES = "${PN}-udev ${PN}-omx-tegra ${PN}-xorg ${PN}-nvstartup"
FILES_${PN}-udev = "${sysconfdir}/udev/rules.d"
FILES_${PN}-xorg = "${sysconfdir}/X11"
FILES_${PN}-omx-tegra = "${sysconfdir}/enctune.conf"
FILES_${PN}-nvstartup = "${sbindir}"
RDEPENDS_${PN}-udev = "udev"
RDEPENDS_${PN}-nvstartup = "bash"
