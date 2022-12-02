DESCRIPTION = "Miscellaneous configuration files provided by L4T"
L4T_DEB_COPYRIGHT_MD5 = "5e719af08f160720e26b2a78670d44bd"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-configs"
TEGRA_LIBRARIES_CONTAINER_CSV = ""

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "\
    nvidia-l4t-init_${PV}_arm64.deb;subdir=${BP};name=init \
    nvidia-l4t-x11_${PV}_arm64.deb;subdir=${BP};name=x11 \
"

MAINSUM = "4863344505848019c7e8c1476837d836ad93797b4585f8e5b79fb425d2745689"
MAINSUM:tegra210 = "6353c82ecd8ccad91aca5a6b96f7b415ff823e151cdf11c66fa9a956290880fc"
INITSUM = "aabb2c405d303a166e31946a51ac39eaf96293a86b40e33a02d9c067ebc3508e"
INITSUM:tegra210 = "af336fe5295030664eef19497fe42279e6f41f2a34b989b0a54266967e4b2f36"
X11SUM = "0ddb0938a2b64ec38871915cac809dfa2b223714763ebb52a48404713f285289"
X11SUM:tegra210 = "68819bb2e35578cf442eeb71799a438afb3348a162ebe72ef152313281441242"
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
