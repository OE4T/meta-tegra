DESCRIPTION = "Tegra-specific firmware from the L4T BSP"
L4T_DEB_COPYRIGHT_MD5 = "e29dee9e20f35e3193d929de8b69873b"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-firmware"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "${@l4t_deb_pkgname(d, 'firmware-openrm')};subdir=${BP};name=openrm"
MAINSUM = "406b309817e0aa71b62996881a46b5f1da39c0007e2e492f56121f726dc0dfff"
OPENRMSUM = "2258dfccd8b1b1a26ce1588e48fb657fc7d27d2a8d00995a3bd1b8738ced967a"
SRC_URI[openrm.sha256sum] = "${OPENRMSUM}"

do_install() {
    install -d ${D}${nonarch_base_libdir}
    cp -R -f ${S}/lib/firmware ${D}${nonarch_base_libdir}
}

PACKAGES = "${PN}-rtl8822 ${PN}-brcm ${PN}-tegra234 ${PN}-tegra264 ${PN}-xusb ${PN}-vic ${PN}"
FILES:${PN}-brcm = "${nonarch_base_libdir}/firmware/brcm ${nonarch_base_libdir}/firmware/bcm4354.hcd ${nonarch_base_libdir}/firmware/nv-*-Version \
                    ${nonarch_base_libdir}/firmware/cypress ${nonarch_base_libdir}/firmware/bcm4359.hcd"
FILES:${PN}-rtl8822 = "${nonarch_base_libdir}/firmware/rtl8822*"
FILES:${PN}-tegra234 = " \
    ${nonarch_base_libdir}/firmware/tegra23x  \
    ${nonarch_base_libdir}/firmware/nvidia/tegra234 \
    ${nonarch_base_libdir}/firmware/nvidia/ga10b \
    ${nonarch_base_libdir}/firmware/nvpva_020.fw \
    ${nonarch_base_libdir}/firmware/dce.bin \
    ${nonarch_base_libdir}/firmware/nvpva_010.fw \
    ${nonarch_base_libdir}/firmware/nvhost_nvdla020.fw \
    ${nonarch_base_libdir}/firmware/nvhost_nvdla030.fw \
    ${nonarch_base_libdir}/firmware/nvhost_nvdla010.fw \
    ${nonarch_base_libdir}/firmware/nvhost_ofa012.fw \
    ${nonarch_base_libdir}/firmware/display-t234-dce.bin \
    ${nonarch_base_libdir}/firmware/nvidia/gv11b \
    ${nonarch_base_libdir}/firmware/nvidia/tegra194 \
    ${nonarch_base_libdir}/firmware/tegra19x_xusb_firmware \
    ${nonarch_base_libdir}/firmware/tegra19x \
"
FILES:${PN}-tegra264 = " \
    ${nonarch_base_libdir}/firmware/rtl8852cu_config \
    ${nonarch_base_libdir}/firmware/nvpva_030.fw \
    ${nonarch_base_libdir}/firmware/nvhost_nvjpg013.fw \
    ${nonarch_base_libdir}/firmware/rtl8852cu_fw \
    ${nonarch_base_libdir}/firmware/nvidia/gb10b \
    ${nonarch_base_libdir}/firmware/nvidia/tegra264 \
    ${nonarch_base_libdir}/firmware/nvidia/580.00 \
    ${nonarch_base_libdir}/firmware/display-t264-dce.bin \
"

FILES:${PN}-xusb = ""
ALLOW_EMPTY:${PN}-xusb = "1"
FILES:${PN}-vic = " \
    ${nonarch_base_libdir}/firmware/nvhost_vic042.fw \
    ${nonarch_base_libdir}/firmware/nvhost_vic051.fw \
    ${nonarch_base_libdir}/firmware/nvhost_vic051.fw.desc \
"
FILES:${PN} = ""
ALLOW_EMPTY:${PN} = "1"
XUSBDEPS = ""
RDEPENDS:${PN}-xusb = "${XUSBDEPS}"
FWDEPS = ""
FWDEPS:tegra234 = "${PN}-tegra234 ${PN}-vic"
FWDEPS:tegra264 = "${PN}-tegra264 ${PN}-vic"
RDEPENDS:${PN} = "${FWDEPS} ${PN}-xusb"
RPROVIDES:${PN}:tegra = "linux-firmware-nvidia-tegra"
RREPLACES:${PN}:tegra = "linux-firmware-nvidia-tegra"
RCONFLICTS:${PN}:tegra = "linux-firmware-nvidia-tegra"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
INSANE_SKIP:${PN}-tegra264 = "arch"
