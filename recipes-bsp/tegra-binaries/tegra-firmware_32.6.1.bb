DESCRIPTION = "Tegra-specific firmware from the L4T BSP"
L4T_DEB_COPYRIGHT_MD5 = "8ccbe1d1f4886f348bc2690c9e743af4"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-firmware"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "nvidia-l4t-xusb-firmware_${L4T_VERSION}-${L4T_BSP_DEB_ORIG_VERSION}_arm64.deb;subdir=${BP};name=xusb"
MAINSUM = "34254de1b1818e0d862582df9af2d1d5828ad40e340614853321be2fdf441cf2"
MAINSUM_tegra210 = "e3a4053a72a11c76d4492b76bc7237bb46585e1e9053a0fdd9ffdb07c8dd75ec"
XUSBSUM = "e36cc0e0b9be88c8524a514a1e26ad0cbce8666067ceead9c18e5854c9586ff1"
XUSBSUM_tegra210 = "55e24027e7b7f66f1ccd78d07a07ec6f40dce4af5553d9c6bd240508cf44ba00"
SRC_URI[xusb.sha256sum] = "${XUSBSUM}"

CONTAINER_CSV_FILES = "${nonarch_base_libdir}/firmware/tegra*"

do_install() {
    install -d ${D}${nonarch_base_libdir}
    cp -R -f ${S}/lib/firmware ${D}${nonarch_base_libdir}
}

do_install_append_tegra210() {
    GPUFWDIR="${D}${nonarch_base_libdir}/firmware/gm20b"
    install -d "$GPUFWDIR"
    for f in acr_ucode.bin fecs.bin fecs_sig.bin gpccs.bin gpmu_ucode.bin \
		   gpmu_ucode_desc.bin gpmu_ucode_image.bin gpu2cde.bin \
		   NETB_img.bin pmu_bl.bin pmu_sig.bin; do
	mv "$GPUFWDIR/../tegra21x/$f" "$GPUFWDIR"
    done
}

PACKAGES = "${PN}-rtl8822 ${PN}-brcm ${PN}-tegra186-xusb ${PN}-tegra194-xusb ${PN}-tegra210-xusb ${PN}-tegra186 ${PN}-tegra194 ${PN}-tegra210 ${PN}-xusb ${PN}"
FILES_${PN}-brcm = "${nonarch_base_libdir}/firmware/brcm ${nonarch_base_libdir}/firmware/bcm4354.hcd ${nonarch_base_libdir}/firmware/nv-*-Version"
FILES_${PN}-rtl8822 = "${nonarch_base_libdir}/firmware/rtl8822*"
FILES_${PN}-tegra186-xusb = "${nonarch_base_libdir}/firmware/tegra18x_xusb_firmware"
FILES_${PN}-tegra194-xusb = "${nonarch_base_libdir}/firmware/tegra19x_xusb_firmware"
FILES_${PN}-tegra210-xusb = "${nonarch_base_libdir}/firmware/tegra21x_xusb_firmware"
FILES_${PN}-tegra186 = "${nonarch_base_libdir}/firmware/tegra18x ${nonarch_base_libdir}/firmware/gp10b"
FILES_${PN}-tegra194 = "${nonarch_base_libdir}/firmware/tegra19x ${nonarch_base_libdir}/firmware/gv11b"
FILES_${PN}-tegra210 = "${nonarch_base_libdir}/firmware/tegra21x ${nonarch_base_libdir}/firmware/gm20b ${nonarch_base_libdir}/firmware/adsp.elf"
INSANE_SKIP_${PN}-tegra210 = "arch"
FILES_${PN}-xusb = ""
ALLOW_EMPTY_${PN}-xusb = "1"
FILES_${PN} = ""
ALLOW_EMPTY_${PN} = "1"
XUSBDEPS = ""
XUSBDEPS_tegra186 = "${PN}-tegra186-xusb"
XUSBDEPS_tegra194 = "${PN}-tegra194-xusb"
XUSBDEPS_tegra210 = "${PN}-tegra210-xusb"
RDEPENDS_${PN}-xusb = "${XUSBDEPS}"
FWDEPS = ""
FWDEPS_tegra186 = "${PN}-tegra186"
FWDEPS_tegra194 = "${PN}-tegra194"
FWDEPS_tegra210 = "${PN}-tegra210"
RDEPENDS_${PN} = "${FWDEPS} ${PN}-xusb"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
