DESCRIPTION = "Miscellaneous configuration files provided by L4T"
L4T_DEB_COPYRIGHT_MD5 = "706bc77ab3b76ec4893260f424e3a475"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-configs"
TEGRA_LIBRARIES_CONTAINER_CSV = ""

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "\
    nvidia-l4t-init_${PV}_arm64.deb;subdir=${BP};name=init \
    nvidia-l4t-x11_${PV}_arm64.deb;subdir=${BP};name=x11 \
"

MAINSUM = "9ffadef17218b911825190228ccf932b7f66f00249b4e92cb710ad89d3026cef"
MAINSUM:tegra210 = "735527299540a9f7d910d7b15b3e31bb8db0480c7693327e90b656ed757dcd66"
INITSUM = "c5d32bc0c84ad79f9266a622aa8c09ec6a230de19a3e35edeb01116cf9506570"
INITSUM:tegra210 = "256b73c87f325ea94600a94f37a7e8fa1c69b8f7b299a616b3876a61738a3472"
X11SUM = "ca98ec6c1989f7f8848c0f9d3f3fc2278e311c832e0e43ec9bd4887db5386a50"
X11SUM:tegra210 = "4497288ecf2772dfcc17fb5a38179f77ebdd18c20b9762e433f8ae17eed67d84"
SRC_URI[init.sha256sum] = "${INITSUM}"
SRC_URI[x11.sha256sum] = "${X11SUM}"

SRC_URI += "\
    file://0001-Patch-udev-rules-for-OE-use.patch \
    file://0002-Patch-nv.sh-script-for-OE-use.patch \
    file://nv-l4t-bootloader-config.sh \
"

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${S}/etc/systemd/nv.sh ${D}${sbindir}/nvstartup
    install -d ${D}/${sysconfdir}/udev/rules.d
    install -m 0644 ${S}/etc/udev/rules.d/99-tegra-devices.rules ${D}${sysconfdir}/udev/rules.d
    install -m 0644 ${S}/etc/udev/rules.d/99-tegra-mmc-ra.rules ${D}${sysconfdir}/udev/rules.d
    install -m 0644 ${S}/etc/udev/rules.d/99-nv-l4t-usb-host-config.rules ${D}${sysconfdir}/udev/rules.d

    install -d ${D}/opt/nvidia/l4t-bootloader-config
    install -m 0755 ${WORKDIR}/nv-l4t-bootloader-config.sh ${D}/opt/nvidia/l4t-bootloader-config/nv-l4t-bootloader-config.sh

    install -d ${D}${sysconfdir}/X11

    install -m 0644 ${S}/etc/enctune.conf ${D}${sysconfdir}
}

do_install:append:tegra186() {
    install -m 0644 ${S}/etc/X11/xorg.conf.t186_ref ${D}${sysconfdir}/X11/xorg.conf
}
do_install:append:tegra194() {
    install -m 0644 ${S}/etc/X11/xorg.conf.t194_ref ${D}${sysconfdir}/X11/xorg.conf
}
do_install:append:tegra210() {
    install -m 0644 ${S}/etc/X11/xorg.conf ${D}${sysconfdir}/X11/
}

PACKAGES = "${PN}-udev ${PN}-omx-tegra ${PN}-xorg ${PN}-nvstartup ${PN}-bootloader"
FILES:${PN}-udev = "${sysconfdir}/udev/rules.d"
FILES:${PN}-xorg = "${sysconfdir}/X11"
FILES:${PN}-omx-tegra = "${sysconfdir}/enctune.conf"
FILES:${PN}-nvstartup = "${sbindir}"
FILES:${PN}-bootloader = "/opt/nvidia/l4t-bootloader-config/"
RDEPENDS:${PN}-udev = "udev"
RDEPENDS:${PN}-nvstartup = "bash"
RDEPENDS_${PN}-bootloader = "bash"
