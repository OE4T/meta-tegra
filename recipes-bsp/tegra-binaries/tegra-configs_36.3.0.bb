DESCRIPTION = "Miscellaneous configuration files provided by L4T"
L4T_DEB_COPYRIGHT_MD5 = "28e8d27ee3bf2210fb4feeb9f8aef427"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-configs"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "\
    ${@l4t_deb_pkgname(d, 'init')};subdir=${BP};name=init \
    ${@l4t_deb_pkgname(d, 'x11')};subdir=${BP};name=x11 \
"

MAINSUM = "f547d4c3e050c2c275ea0054b3d8693fafdfbc0cf1c01ba2c5ec26cb466d7243"
INITSUM = "268a9d974765ccf83dec9a0a94e2870c5c789334701bae3a3321c83795c6b431"
X11SUM = "a3eace1df4921dd804947ded77b28cf73c675894daf144901d7c2ed797c2de20"
SRC_URI[init.sha256sum] = "${INITSUM}"
SRC_URI[x11.sha256sum] = "${X11SUM}"

SRC_URI += "\
    file://0001-Patch-udev-rules-for-OE-use.patch \
    file://0002-Patch-nv.sh-script-for-OE-use.patch \
    file://nv-l4t-bootloader-config.sh \
    file://devices.csv \
    file://drivers.csv \
"

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${S}/etc/systemd/nv.sh ${D}${sbindir}/nvstartup
    install -d ${D}/${sysconfdir}/udev/rules.d
    install -m 0644 ${S}/etc/udev/rules.d/99-tegra-devices.rules ${D}${sysconfdir}/udev/rules.d
    install -m 0644 ${S}/etc/udev/rules.d/99-tegra-mmc-ra.rules ${D}${sysconfdir}/udev/rules.d
    install -m 0644 ${S}/etc/udev/rules.d/99-nv-l4t-usb-host-config.rules ${D}${sysconfdir}/udev/rules.d

    install -d ${D}/opt/nvidia/l4t-bootloader-config
    install -m 0755 ${UNPACKDIR}/nv-l4t-bootloader-config.sh ${D}/opt/nvidia/l4t-bootloader-config/nv-l4t-bootloader-config.sh

    install -d ${D}${sysconfdir}/X11

    install -m 0644 ${S}/etc/enctune.conf ${D}${sysconfdir}

    install -d ${D}${sysconfdir}/sysctl.d
    install -m 0644 ${S}/etc/sysctl.d/60-nvsciipc.conf ${D}${sysconfdir}/sysctl.d/

    install -d ${D}${sysconfdir}/modprobe.d
    install -m 0644 ${S}/etc/modprobe.d/denylist*.conf ${D}${sysconfdir}/modprobe.d/

    # We use a statically generated file by using 
    # https://gist.github.com/dwalkes/0e2dea422f2df93bcc9badc0512a6855
    # and oe-pkgdata-util file-path <libname> for oe4t-missing.csv 
    # libraries generated from the script and few hand-modified changes
    # Removed *.json file as this created errors
    # Please create an issue for a missing file in the passthrough
    # FIXME: create a mechanism to dynamically generate l4t.csv based on the installed libraries
    install -d ${D}${sysconfdir}/nvidia-container-runtime/host-files-for-container.d
    install -m 0644 ${UNPACKDIR}/drivers.csv ${D}${sysconfdir}/nvidia-container-runtime/host-files-for-container.d
    install -m 0644 ${UNPACKDIR}/devices.csv ${D}${sysconfdir}/nvidia-container-runtime/host-files-for-container.d
}

do_install:append:tegra234() {
    install -m 0644 ${S}/etc/X11/xorg.conf ${D}${sysconfdir}/X11/xorg.conf
    install -d ${D}${sysconfdir}/X11/xorg.conf.d
    install -m 0644 ${S}/etc/X11/xorg.conf.d/tegra-drm-outputclass.conf ${D}${sysconfdir}/X11/xorg.conf.d
}

PACKAGES = "${PN}-udev ${PN}-omx-tegra ${PN}-xorg ${PN}-nvstartup ${PN}-container-csv ${PN}-bootloader"
FILES:${PN}-udev = "${sysconfdir}/udev/rules.d ${sysconfdir}/modprobe.d"
FILES:${PN}-xorg = "${sysconfdir}/X11"
FILES:${PN}-omx-tegra = "${sysconfdir}/enctune.conf"
FILES:${PN}-nvstartup = "${sbindir} ${sysconfdir}/sysctl.d"
FILES:${PN}-container-csv = "${sysconfdir}/nvidia-container-runtime"
FILES:${PN}-bootloader = "/opt/nvidia/l4t-bootloader-config"
RDEPENDS:${PN}-udev = "udev"
RDEPENDS:${PN}-nvstartup = "bash"
RDEPENDS:${PN}-bootloader = "bash"
