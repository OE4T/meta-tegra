DESCRIPTION = "Miscellaneous configuration files provided by L4T"
L4T_DEB_COPYRIGHT_MD5 = "5e719af08f160720e26b2a78670d44bd"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-configs"
TEGRA_LIBRARIES_CONTAINER_CSV = ""

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "\
    nvidia-l4t-init_${PV}_arm64.deb;subdir=${BP};name=init \
    nvidia-l4t-x11_${PV}_arm64.deb;subdir=${BP};name=x11 \
"

MAINSUM = "37d34c98b33ac5d216170c3c9fb059497f0bfbfe9ac48778c9447ba92bcf83c3"
MAINSUM_tegra210 = "602cc78e530ca919c298dc542b01b4f90cbcfc35412fe064b9704d2676ffbf6c"
INITSUM = "0167fba98b85dbf7e51571bc23f28b8f79377877ed98bb30097dc02a11ddb85e"
INITSUM_tegra210 = "b930532c06af7e6dec08ab77af422dc8040b9e71d8bfa3c6cec2159cb4e309d1"
X11SUM = "94a0beea19d40f919170dabdf7e42b6cc6f2b0f76c68d3673d5366fb26d0782e"
X11SUM_tegra210 = "e12c3b1c21b7989e094ec50b366f95de384ae8e72e6ad121840e90b6b6fc652e"
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
