DESCRIPTION = "Tegra-specific firmware from the L4T BSP"
L4T_DEB_COPYRIGHT_MD5 = "90328d200c192774c9f168bf103b193e"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-firmware"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "${@l4t_deb_pkgname(d, 'xusb-firmware')};subdir=${BP};name=xusb"
MAINSUM = "917eabedf603d94f0d840673d300db92eaacb82a8a23a243030665ede5a8f367"
XUSBSUM = "4b076e828f4f76ec35a973e80bca349c95d8fcf8876d0986d914aa63ade08aed"
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
