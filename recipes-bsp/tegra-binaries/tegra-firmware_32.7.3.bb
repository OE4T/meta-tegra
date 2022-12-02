DESCRIPTION = "Tegra-specific firmware from the L4T BSP"
L4T_DEB_COPYRIGHT_MD5 = "8ccbe1d1f4886f348bc2690c9e743af4"

L4T_DEB_TRANSLATED_BPN = "nvidia-l4t-firmware"

require tegra-debian-libraries-common.inc

SRC_SOC_DEBS += "nvidia-l4t-xusb-firmware_${PV}_arm64.deb;subdir=${BP};name=xusb"
MAINSUM = "35ee3951106443c22deb3d73db4310d7728817804252154614bbcf6f33c2a924"
MAINSUM:tegra210 = "78e14cce9d2c565b333be0bf9d80d3fc1b96c5e2d9c0aed7834d94454b37ee1b"
XUSBSUM = "f3cbba1aedd2f4486fb2edbd59b70655232eff03703905c871301041cca2cdd2"
XUSBSUM:tegra210 = "4e4e5d4440e16a89f659099b0f003989db58a287e50568b1e876dcad6a31e2ba"
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
