DESCRIPTION = "Tegra-specific firmware from the L4T BSP"
L4T_DEB_COPYRIGHT_MD5 = "e29dee9e20f35e3193d929de8b69873b"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-firmware"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "${@l4t_deb_pkgname(d, 'xusb-firmware')};subdir=${BP};name=xusb"
MAINSUM = "23868b5ff85ed4b6198e22b60ad0f846e2a5ba9f068bb62bdc1b8a8133a92b8e"
XUSBSUM = "788dcd739fcc0ea3e37fb41b7dd43458ccc73ff614ac5c56aa690b752cbaff03"
SRC_URI[xusb.sha256sum] = "${XUSBSUM}"

do_install() {
    install -d ${D}${nonarch_base_libdir}
    cp -R -f ${S}/lib/firmware ${D}${nonarch_base_libdir}
}

PACKAGES = "${PN}-rtl8822 ${PN}-brcm ${PN}-tegra194-xusb ${PN}-tegra194 ${PN}-tegra234 ${PN}-xusb ${PN}-vic ${PN}"
FILES:${PN}-brcm = "${nonarch_base_libdir}/firmware/brcm ${nonarch_base_libdir}/firmware/bcm4354.hcd ${nonarch_base_libdir}/firmware/nv-*-Version \
                    ${nonarch_base_libdir}/firmware/cypress ${nonarch_base_libdir}/firmware/bcm4359.hcd"
FILES:${PN}-rtl8822 = "${nonarch_base_libdir}/firmware/rtl8822*"
FILES:${PN}-tegra194-xusb = "${nonarch_base_libdir}/firmware/tegra19x_xusb_firmware ${nonarch_base_libdir}/firmware/nvidia/tegra194/xusb.bin"
FILES:${PN}-tegra194 = "${nonarch_base_libdir}/firmware/tegra19x ${nonarch_base_libdir}/firmware/gv11b ${nonarch_base_libdir}/firmware/nvidia/tegra194 ${nonarch_base_libdir}/firmware/nvhost_nvdla010.fw ${nonarch_base_libdir}/firmware/nvpva_010.fw"
FILES:${PN}-tegra234 = "${nonarch_base_libdir}/firmware/tegra23x ${nonarch_base_libdir}/firmware/ga10b ${nonarch_base_libdir}/firmware/nvpva_020.fw ${nonarch_base_libdir}/firmware/dce.bin ${nonarch_base_libdir}/firmware/nvhost_nvdla020.fw \
		        ${nonarch_base_libdir}/firmware/nvhost_ofa012.fw ${nonarch_base_libdir}/firmware/display-t234-dce.bin"
FILES:${PN}-xusb = ""
ALLOW_EMPTY:${PN}-xusb = "1"
FILES:${PN}-vic = "${nonarch_base_libdir}/firmware/nvhost_vic042.fw"
FILES:${PN} = ""
ALLOW_EMPTY:${PN} = "1"
XUSBDEPS = ""
XUSBDEPS:tegra194 = "${PN}-tegra194-xusb"
RDEPENDS:${PN}-xusb = "${XUSBDEPS}"
FWDEPS = ""
FWDEPS:tegra194 = "${PN}-tegra194 ${PN}-vic"
FWDEPS:tegra234 = "${PN}-tegra234 ${PN}-vic"
RDEPENDS:${PN} = "${FWDEPS} ${PN}-xusb"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
