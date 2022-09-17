DESCRIPTION = "Miscellaneous configuration files provided by L4T"
L4T_DEB_COPYRIGHT_MD5 = "5e719af08f160720e26b2a78670d44bd"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-configs"
TEGRA_LIBRARIES_CONTAINER_CSV = ""

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "\
    nvidia-l4t-init_${PV}_arm64.deb;subdir=${BP};name=init \
    nvidia-l4t-x11_${PV}_arm64.deb;subdir=${BP};name=x11 \
"

<<<<<<<< HEAD:recipes-bsp/tegra-binaries/tegra-configs_32.7.2.bb
MAINSUM = "5583b9cae938cba31272d14f7f723c2514f8b64caf85f4d4ecec07525358b540"
MAINSUM:tegra210 = "e81d5c88d86bda285e9ddfee1262024977247a79b11cb4aed84f5a9d0f28d524"
INITSUM = "44fa43d9646d66b5c1bb28def9736c3d8cf7a3e21082fbf9017c63246e2b0ae1"
INITSUM:tegra210 = "a21c44851b287f0fde245f753fa7f591a2d84f125b111bbf54ae34d7c0f3b255"
X11SUM = "79259130421b45fb379fd1df39744ea2212c1f0bdc8102d799dc78afa3902371"
X11SUM:tegra210 = "c1d018921f98728a5835e68fc8c2836c419f8de54db27738c62488d67df77942"
========
MAINSUM = "ff8ee3b3f9dfc44e2cddc8990a6c60291813b948d588e5496e099588341b7baf"
INITSUM = "aae9f9cf02fd0a0159f772b3582422164989131617a971df8ebdbff1339c59fd"
X11SUM = "829dbc1a16e4e3408517074d2595a65c29b74e4d0dbfbb3405854a02d4a995ba"
>>>>>>>> 390a51f3 (tegra-binaries: update for 34.1.0):recipes-bsp/tegra-binaries/tegra-configs_34.1.0.bb
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

    install -d ${D}${sysconfdir}/sysctl.d
    install -m 0644 ${S}/etc/sysctl.d/60-nvsciipc.conf ${D}${sysconfdir}/sysctl.d/

    install -d ${D}${sysconfdir}/nvidia-container-runtime/host-files-for-container.d
    grep '^dev,' ${S}/etc/nvidia-container-runtime/host-files-for-container.d/l4t.csv > ${D}${sysconfdir}/nvidia-container-runtime/host-files-for-container.d/l4t.csv
    chmod 0644 ${D}${sysconfdir}/nvidia-container-runtime/host-files-for-container.d/l4t.csv
}

do_install:append:tegra194() {
    install -m 0644 ${S}/etc/X11/xorg.conf.t194_ref ${D}${sysconfdir}/X11/xorg.conf
}

do_install:append:tegra234() {
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
RDEPENDS_${PN}-bootloader = "bash"
