DESCRIPTION = "Tegra-specific firmware from the L4T BSP"
L4T_DEB_COPYRIGHT_MD5 = "b14cc54c1ac58b297ede64cb496fd837"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-firmware"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "${@l4t_deb_pkgname(d, 'xusb-firmware')};subdir=${BP};name=xusb"
MAINSUM = "33172c7bb6e46d71bdf7d0b7608c1007ea55bb772da452ecb4a113bc818db303"
XUSBSUM = "774a25b509f27d8bc39dc87e743e5ebcb0e5c8a14e4f271255591e87083a3d24"
SRC_URI[xusb.sha256sum] = "${XUSBSUM}"

do_install() {
    install -d ${D}${nonarch_base_libdir}
    cp -R -f ${S}/lib/firmware ${D}${nonarch_base_libdir}
}

PACKAGES = "${PN}-rtl8822 ${PN}-brcm ${PN}-tegra194-xusb ${PN}-tegra234 ${PN}-xusb ${PN}-vic ${PN}"
FILES:${PN}-brcm = "${nonarch_base_libdir}/firmware/brcm ${nonarch_base_libdir}/firmware/bcm4354.hcd ${nonarch_base_libdir}/firmware/nv-*-Version \
                    ${nonarch_base_libdir}/firmware/cypress ${nonarch_base_libdir}/firmware/bcm4359.hcd"
FILES:${PN}-rtl8822 = "${nonarch_base_libdir}/firmware/rtl8822*"
FILES:${PN}-tegra194-xusb = "${nonarch_base_libdir}/firmware/tegra19x_xusb_firmware ${nonarch_base_libdir}/firmware/nvidia/tegra194/xusb.bin"
FILES:${PN}-tegra234 = "${nonarch_base_libdir}/firmware/tegra23x ${nonarch_base_libdir}/firmware/ga10b ${nonarch_base_libdir}/firmware/nvpva_020.fw ${nonarch_base_libdir}/firmware/dce.bin ${nonarch_base_libdir}/firmware/nvhost_nvdla020.fw \
		        ${nonarch_base_libdir}/firmware/nvhost_ofa012.fw ${nonarch_base_libdir}/firmware/display-t234-dce.bin"
FILES:${PN}-xusb = ""
ALLOW_EMPTY:${PN}-xusb = "1"
FILES:${PN}-vic = "${nonarch_base_libdir}/firmware/nvhost_vic042.fw"
FILES:${PN} = ""
ALLOW_EMPTY:${PN} = "1"
XUSBDEPS = ""
RDEPENDS:${PN}-xusb = "${XUSBDEPS}"
FWDEPS = ""
FWDEPS:tegra234 = "${PN}-tegra234 ${PN}-vic"
RDEPENDS:${PN} = "${FWDEPS} ${PN}-xusb"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
