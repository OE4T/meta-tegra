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
    file://0001-tegra-configs-fix-path-to-nvpower.sh.patch \
    file://nv-load-display-modules.sh.in \
    file://nv-load-display-modules.conf.in \
    file://devices.csv \
    file://drivers.csv \
"

GPU_VARIANT = "UNKNOWN"
GPU_VARIANT:tegra234 = "nvgpu-l4t"
GPU_VARIANT:tegra264 = "openrm-l4t"
XORG_CONFIG = "UNKNOWN"
XORG_CONFIG:tegra234 = "xorg.conf"
XORG_CONFIG:tegra264 = "xorg.conf.pci"

TEGRA_SYSRQ_ENABLE ??= "0"

B = "${WORKDIR}/build"

do_compile() {
    sed -r -e's,^(kernel\.sysrq =).*,\1 ${TEGRA_SYSRQ_ENABLE},' ${S}/etc/sysctl.d/90-tegra-settings.conf > ${B}/90-tegra-settings.conf
    sed -e's,@VARIANT@,${GPU_VARIANT},g' ${UNPACKDIR}/nv-load-display-modules.sh.in > ${B}/nv-load-display-modules.sh
    sed -e's,@LIBEXECDIR@,${libexecdir},g' ${UNPACKDIR}/nv-load-display-modules.conf.in > ${B}/nv-load-display-modules.conf
}

do_install() {
    install -m 0644 -D -t ${D}/${sysconfdir}/udev/rules.d \
	    ${S}/etc/udev/rules.d/99-tegra-devices.rules \
	    ${S}/etc/udev/rules.d/99-tegra-mmc-ra.rules \
	    ${S}/etc/udev/rules.d/99-nv-l4t-usb-host-config.rules

    install -m 0644 -D -t ${D}${sysconfdir}/sysctl.d ${B}/90-tegra-settings.conf

    install -m 0644 -D -t ${D}${sysconfdir}/nvidia-container-runtime/host-files-for-container.d ${UNPACKDIR}/drivers.csv ${UNPACKDIR}/devices.csv

    install -m 0644 -D ${S}/opt/nvidia/nv-disp-module-configs/nv-modprobe-${GPU_VARIANT}-display.conf ${D}${sysconfdir}/modprobe.d/nv-display.conf
    install -m 0644 -D ${S}/opt/nvidia/nv-disp-module-configs/nv-depmod-${GPU_VARIANT}-display.conf -t ${D}${sysconfdir}/depmod.d/nv-display.conf
    if [ "${GPU_VARIANT}" = "nvgpu-l4t" ]; then
        install -m 0644 ${S}/etc/modprobe.d/nvgpu.conf ${D}${sysconfdir}/modprobe.d/
    fi

    install -m 0755 -D ${B}/nv-load-display-modules.sh ${D}${libexecdir}/nv-load-display-modules
    install -m 0644 -D -t ${D}${sysconfdir}/systemd/system/systemd-modules-load.service.d ${B}/nv-load-display-modules.conf

    install -m 0644 -D ${S}/etc/X11/${XORG_CONFIG} ${D}${sysconfdir}/X11/xorg.conf
    install -m 0644 -D -t ${D}${sysconfdir}/X11/xorg.conf.d ${S}/etc/X11/xorg.conf.d/tegra-drm-outputclass.conf
}

PACKAGES = " ${PN}-display-driver ${PN}-udev ${PN}-xorg ${PN}-sysctl ${PN}-container-csv"
FILES:${PN}-display-driver = "${sysconfdir}/modprobe.d/nv-modprobe* ${sysconfdir}/modprobe.d/nvgpu.conf ${sysconfdir}/depmod.d \
			      ${sysconfdir}/systemd/system/systemd-modules-load.service.d ${libexecdir}/nv-load-display-modules"
FILES:${PN}-udev = "${sysconfdir}/udev/rules.d ${sysconfdir}/modprobe.d"
FILES:${PN}-xorg = "${sysconfdir}/X11"
FILES:${PN}-sysctl = "${sysconfdir}/sysctl.d"
FILES:${PN}-container-csv = "${sysconfdir}/nvidia-container-runtime"
RDEPENDS:${PN}-udev = "udev bash"

USERADD_PACKAGES = "${PN}-udev"
GROUPADD_PARAM:${PN}-udev = "--system debug"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
