DESCRIPTION = "Miscellaneous configuration files provided by L4T"
L4T_DEB_COPYRIGHT_MD5 = "1d8bcc1d206e96f3fcc037f6686b9946"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-configs"

require tegra-debian-libraries-common.inc

inherit useradd

SRC_SOC_DEBS += "\
    ${@l4t_deb_pkgname(d, 'init')};subdir=${BP};name=init \
    ${@l4t_deb_pkgname(d, 'x11')};subdir=${BP};name=x11 \
    ${@l4t_deb_pkgname(d, 'init-openrm')};subdir=${BP};name=openrm \
"

MAINSUM = "001f02d92049de064c2d4922ade60958d9268527b7277db4e8eda77bad522942"
INITSUM = "95f5f1c860fed1bf353919eb979b3f67301acdbe5abae8c964edebad405a737e"
X11SUM = "265ec55f894d1b32c74b82b15d8bcd8113c2b1d568fb00b956b9382a5dafd5c2"
OPENRMSUM = "c7fdf77dd0c6bdfa82cbb6987ece8512198164d8423d4013204b33a6cc537a44"

SRC_URI[init.sha256sum] = "${INITSUM}"
SRC_URI[x11.sha256sum] = "${X11SUM}"
SRC_URI[openrm.sha256sum] = "${OPENRMSUM}"

SRC_URI += "\
    file://0001-Patch-nv-graphics.sh-script-for-OE-use.patch \
    file://0001-tegra-configs-fix-path-to-nvpower.sh.patch \
    file://nv-l4t-bootloader-config.sh \
    file://devices.csv \
    file://drivers.csv \
"

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${S}/etc/systemd/nv-graphics.sh ${D}${sbindir}/nvstartup
    install -d ${D}/${sysconfdir}/udev/rules.d
    install -m 0644 ${S}/etc/udev/rules.d/99-tegra-devices.rules ${D}${sysconfdir}/udev/rules.d
    install -m 0644 ${S}/etc/udev/rules.d/99-tegra-mmc-ra.rules ${D}${sysconfdir}/udev/rules.d
    install -m 0644 ${S}/etc/udev/rules.d/99-nv-l4t-usb-host-config.rules ${D}${sysconfdir}/udev/rules.d

    install -d ${D}/opt/nvidia/l4t-bootloader-config
    install -m 0755 ${UNPACKDIR}/nv-l4t-bootloader-config.sh ${D}/opt/nvidia/l4t-bootloader-config/nv-l4t-bootloader-config.sh

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
    install -d ${D}${sysconfdir}/X11
    install -m 0644 ${S}/etc/X11/xorg.conf ${D}${sysconfdir}/X11/xorg.conf
    install -d ${D}${sysconfdir}/X11/xorg.conf.d
    install -m 0644 ${S}/etc/X11/xorg.conf.d/tegra-drm-outputclass.conf ${D}${sysconfdir}/X11/xorg.conf.d
    install -d ${D}${sysconfdir}/modprobe.d
    install -m 0644 ${S}/etc/modprobe.d/nvgpu.conf ${D}${sysconfdir}/modprobe.d/
}

do_install:append:tegra264() {
    install -d ${D}${sysconfdir}/X11
    install -m 0644 ${S}/etc/X11/xorg.conf.pci ${D}${sysconfdir}/X11/xorg.conf
    install -d ${D}${sysconfdir}/X11/xorg.conf.d
    install -m 0644 ${S}/etc/X11/xorg.conf.d/tegra-drm-outputclass.conf ${D}${sysconfdir}/X11/xorg.conf.d
}

PACKAGES = "${PN}-udev ${PN}-xorg ${PN}-nvstartup ${PN}-container-csv ${PN}-bootloader"
FILES:${PN}-udev = "${sysconfdir}/udev/rules.d ${sysconfdir}/modprobe.d"
FILES:${PN}-xorg = "${sysconfdir}/X11"
FILES:${PN}-nvstartup = "${sbindir} ${sysconfdir}/sysctl.d"
FILES:${PN}-container-csv = "${sysconfdir}/nvidia-container-runtime"
FILES:${PN}-bootloader = "/opt/nvidia/l4t-bootloader-config"
RDEPENDS:${PN}-udev = "udev"
RDEPENDS:${PN}-nvstartup = "bash"
RDEPENDS:${PN}-bootloader = "bash"

USERADD_PACKAGES = "${PN}-udev"
GROUPADD_PARAM:${PN}-udev = "--system debug"
