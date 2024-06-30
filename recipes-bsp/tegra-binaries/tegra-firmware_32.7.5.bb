DESCRIPTION = "Tegra-specific firmware from the L4T BSP"
L4T_DEB_COPYRIGHT_MD5 = "9d0203dba0bd9e7dff108bbfea124e7c"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-firmware"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "nvidia-l4t-xusb-firmware_${PV}_arm64.deb;subdir=${BP};name=xusb"
MAINSUM = "dc9401be553edf3881f4696cecc92b02c3088409da28e5d4717ca520afa44bad"
MAINSUM:tegra210 = "485be42e49c838f31e32eed196a2eaef6a7133512e88c37afb4e3b4b81316934"
XUSBSUM = "5d7e8ea4d8db37f6aafef174b126131e7c726d66d2f254f955898f3952ce16b3"
XUSBSUM:tegra210 = "322748dd484542c4eb107b70d1fe0b383b5bee20d35f6acfdde1ec22a2e3d75a"
SRC_URI[xusb.sha256sum] = "${XUSBSUM}"

CONTAINER_CSV_FILES = "${nonarch_base_libdir}/firmware/tegra*"

do_install() {
    install -d ${D}${nonarch_base_libdir}
    cp -R -f ${S}/lib/firmware ${D}${nonarch_base_libdir}
}

do_install:append:tegra210() {
    GPUFWDIR="${D}${nonarch_base_libdir}/firmware/gm20b"
    install -d "$GPUFWDIR"
    for f in fecs.bin fecs_sig.bin gpccs.bin gpmu_ucode.bin \
		   gpmu_ucode_desc.bin gpmu_ucode_image.bin gpu2cde.bin \
		   NETB_img.bin nv_acr_ucode_prod.bin pmu_bl.bin pmu_sig.bin; do
	mv "$GPUFWDIR/../tegra21x/$f" "$GPUFWDIR"
    done
}

PACKAGES = "${PN}-rtl8822 ${PN}-brcm ${PN}-tegra186-xusb ${PN}-tegra194-xusb ${PN}-tegra210-xusb ${PN}-tegra186 ${PN}-tegra194 ${PN}-tegra210 ${PN}-xusb ${PN}"
FILES:${PN}-brcm = "${nonarch_base_libdir}/firmware/brcm ${nonarch_base_libdir}/firmware/bcm4354.hcd ${nonarch_base_libdir}/firmware/nv-*-Version"
FILES:${PN}-rtl8822 = "${nonarch_base_libdir}/firmware/rtl8822*"
FILES:${PN}-tegra186-xusb = "${nonarch_base_libdir}/firmware/tegra18x_xusb_firmware"
FILES:${PN}-tegra194-xusb = "${nonarch_base_libdir}/firmware/tegra19x_xusb_firmware"
FILES:${PN}-tegra210-xusb = "${nonarch_base_libdir}/firmware/tegra21x_xusb_firmware"
FILES:${PN}-tegra186 = "${nonarch_base_libdir}/firmware/tegra18x ${nonarch_base_libdir}/firmware/gp10b"
FILES:${PN}-tegra194 = "${nonarch_base_libdir}/firmware/tegra19x ${nonarch_base_libdir}/firmware/gv11b"
FILES:${PN}-tegra210 = "${nonarch_base_libdir}/firmware/tegra21x ${nonarch_base_libdir}/firmware/gm20b ${nonarch_base_libdir}/firmware/adsp.elf"
INSANE_SKIP:${PN}-tegra210 = "arch"
FILES:${PN}-xusb = ""
ALLOW_EMPTY:${PN}-xusb = "1"
FILES:${PN} = ""
ALLOW_EMPTY:${PN} = "1"
XUSBDEPS = ""
XUSBDEPS:tegra186 = "${PN}-tegra186-xusb"
XUSBDEPS:tegra194 = "${PN}-tegra194-xusb"
XUSBDEPS:tegra210 = "${PN}-tegra210-xusb"
RDEPENDS:${PN}-xusb = "${XUSBDEPS}"
FWDEPS = ""
FWDEPS:tegra186 = "${PN}-tegra186"
FWDEPS:tegra194 = "${PN}-tegra194"
FWDEPS:tegra210 = "${PN}-tegra210"
RDEPENDS:${PN} = "${FWDEPS} ${PN}-xusb"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
