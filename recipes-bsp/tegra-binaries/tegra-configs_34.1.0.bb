DESCRIPTION = "Miscellaneous configuration files provided by L4T"
L4T_DEB_COPYRIGHT_MD5 = "5e719af08f160720e26b2a78670d44bd"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-configs"
TEGRA_LIBRARIES_CONTAINER_CSV = ""

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "\
    nvidia-l4t-init_${PV}_arm64.deb;subdir=${BP};name=init \
    nvidia-l4t-x11_${PV}_arm64.deb;subdir=${BP};name=x11 \
"

MAINSUM = "ff8ee3b3f9dfc44e2cddc8990a6c60291813b948d588e5496e099588341b7baf"
INITSUM = "aae9f9cf02fd0a0159f772b3582422164989131617a971df8ebdbff1339c59fd"
X11SUM = "829dbc1a16e4e3408517074d2595a65c29b74e4d0dbfbb3405854a02d4a995ba"
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

    install -d ${D}${sysconfdir}/sysctl.d
    install -m 0644 ${S}/etc/sysctl.d/60-nvsciipc.conf ${D}${sysconfdir}/sysctl.d/

    install -d ${D}${sysconfdir}/nvidia-container-runtime/host-files-for-container.d
    grep '^dev,' ${S}/etc/nvidia-container-runtime/host-files-for-container.d/l4t.csv > ${D}${sysconfdir}/nvidia-container-runtime/host-files-for-container.d/l4t.csv
    chmod 0644 ${D}${sysconfdir}/nvidia-container-runtime/host-files-for-container.d/l4t.csv
}

do_install:append:tegra194() {
    install -m 0644 ${S}/etc/X11/xorg.conf.t194_ref ${D}${sysconfdir}/X11/xorg.conf
}

do_install:append:tegra243() {
    install -m 0644 ${S}/etc/X11/xorg.conf ${D}${sysconfdir}/X11/xorg.conf
}

PACKAGES = "${PN}-udev ${PN}-omx-tegra ${PN}-xorg ${PN}-nvstartup ${PN}-container-csv"
FILES:${PN}-udev = "${sysconfdir}/udev/rules.d"
FILES:${PN}-xorg = "${sysconfdir}/X11"
FILES:${PN}-omx-tegra = "${sysconfdir}/enctune.conf"
FILES:${PN}-nvstartup = "${sbindir} ${sysconfdir}/sysctl.d"
FILES:${PN}-container-csv = "${sysconfdir}/nvidia-container-runtime"
RDEPENDS:${PN}-udev = "udev"
RDEPENDS:${PN}-nvstartup = "bash"
