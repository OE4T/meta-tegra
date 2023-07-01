DESCRIPTION = "Tegra-specific firmware from the L4T BSP"
L4T_DEB_COPYRIGHT_MD5 = "8ccbe1d1f4886f348bc2690c9e743af4"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-firmware"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "nvidia-l4t-xusb-firmware_${PV}_arm64.deb;subdir=${BP};name=xusb"
MAINSUM = "3cd3cf6ae0d86bed0cd6357facc9dd5b303463107861bf16c2a7aa34e03f84e3"
MAINSUM:tegra210 = "349cb2b2b8197107c4ae07667e0410f026db3b9bacc04b715f890e1103968226"
XUSBSUM = "aa4a9c4aabc1e2b90143bb50f6ae75a35eb1df5d74c956f6638501e84c3e6ca2"
XUSBSUM:tegra210 = "f0c22e7e554afd89a76f0f9658be11b5a837e647c1723b0280985d0f5f587e64"
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
