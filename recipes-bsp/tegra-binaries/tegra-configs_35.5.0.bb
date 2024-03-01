DESCRIPTION = "Miscellaneous configuration files provided by L4T"
L4T_DEB_COPYRIGHT_MD5 = "28e8d27ee3bf2210fb4feeb9f8aef427"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-configs"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "\
    ${@l4t_deb_pkgname(d, 'init')};subdir=${BP};name=init \
    ${@l4t_deb_pkgname(d, 'x11')};subdir=${BP};name=x11 \
"

MAINSUM = "ebd0dc5b8a8ae74ee882ecd398ab11ea4604cb3d9a1d183d84336a3fb59e6e16"
INITSUM = "9d9e5d2c912e9432ba189e6702e0135a72db725a8ee22db3f8475d9c9374b77d"
X11SUM = "d44320a4435a490ec5b3948f0c643ac68cc9302681bfd68fb5dcf2a1c7985866"
SRC_URI[init.sha256sum] = "${INITSUM}"
SRC_URI[x11.sha256sum] = "${X11SUM}"

SRC_URI += "\
    file://0001-Patch-udev-rules-for-OE-use.patch \
    file://0002-Patch-nv.sh-script-for-OE-use.patch \
    file://nv-l4t-bootloader-config.sh \
    file://l4t.csv \
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

    # We use a statically generated file by using 
    # https://gist.github.com/dwalkes/0e2dea422f2df93bcc9badc0512a6855
    # and oe-pkgdata-util file-path <libname> for oe4t-missing.csv 
    # libraries generated from the script and few hand-modified changes
    # Removed *.json file as this created errors
    # Please create an issue for a missing file in the passthrough
    # FIXME: create a mechanism to dynamically generate l4t.csv based on the installed libraries
    install -d ${D}${sysconfdir}/nvidia-container-runtime/host-files-for-container.d
    install -m 0644 ${WORKDIR}/l4t.csv ${D}${sysconfdir}/nvidia-container-runtime/host-files-for-container.d
}

do_install:append:tegra194() {
    install -m 0644 ${S}/etc/X11/xorg.conf.t194_ref ${D}${sysconfdir}/X11/xorg.conf
}

do_install:append:tegra234() {
    install -m 0644 ${S}/etc/X11/xorg.conf ${D}${sysconfdir}/X11/xorg.conf
    install -d ${D}${sysconfdir}/X11/xorg.conf.d
    install -m 0644 ${S}/etc/X11/xorg.conf.d/tegra-drm-outputclass.conf ${D}${sysconfdir}/X11/xorg.conf.d
}

PACKAGES = "${PN}-udev ${PN}-omx-tegra ${PN}-xorg ${PN}-nvstartup ${PN}-container-csv ${PN}-bootloader"
FILES:${PN}-udev = "${sysconfdir}/udev/rules.d"
FILES:${PN}-xorg = "${sysconfdir}/X11"
FILES:${PN}-omx-tegra = "${sysconfdir}/enctune.conf"
FILES:${PN}-nvstartup = "${sbindir} ${sysconfdir}/sysctl.d"
FILES:${PN}-container-csv = "${sysconfdir}/nvidia-container-runtime"
FILES:${PN}-bootloader = "/opt/nvidia/l4t-bootloader-config"
RDEPENDS:${PN}-udev = "udev"
RDEPENDS:${PN}-nvstartup = "bash"
RDEPENDS:${PN}-bootloader = "bash"
