require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

do_configure() {
    tar -C ${B} -x -f ${S}/nv_tegra/nvidia_drivers.tbz2 lib/firmware
}

do_compile[noexec] = "1"

do_install() {
    install -d ${D}${nonarch_base_libdir}
    cp -R -f ${B}/lib/firmware ${D}${nonarch_base_libdir}
}

do_install_append_tegra210() {
    GPUFWDIR="${D}${nonarch_base_libdir}/firmware/gm20b"
    install -d "$GPUFWDIR"
    for f in acr_ucode.bin fecs.bin fecs_sig.bin gpccs.bin gpmu_ucode.bin \
		   gpmu_ucode_desc.bin gpmu_ucode_image.bin gpu2cde.bin \
		   NETB_img.bin pmu_bl.bin pmu_sig.bin; do
	mv "$GPUFWDIR/../tegra21x/$f" "$GPUFWDIR"
    done
    cd "$oldwd"
}

PACKAGES = "${PN}-brcm ${PN}-tegra186-xusb ${PN}-tegra194-xusb ${PN}-tegra210-xusb ${PN}-tegra186 ${PN}-tegra194 ${PN}-tegra210 ${PN}-xusb ${PN}"
FILES_${PN}-brcm = "${nonarch_base_libdir}/firmware/brcm ${nonarch_base_libdir}/firmware/bcm4354.hcd ${nonarch_base_libdir}/firmware/nv-BT-Version"
FILES_${PN}-tegra186-xusb = "${nonarch_base_libdir}/firmware/tegra18x_xusb_firmware"
FILES_${PN}-tegra194-xusb = "${nonarch_base_libdir}/firmware/tegra19x_xusb_firmware"
FILES_${PN}-tegra210-xusb = "${nonarch_base_libdir}/firmware/tegra21x_xusb_firmware"
FILES_${PN}-tegra186 = "${nonarch_base_libdir}/firmware/tegra18x ${nonarch_base_libdir}/firmware/gp10b"
FILES_${PN}-tegra194 = "${nonarch_base_libdir}/firmware/tegra19x ${nonarch_base_libdir}/firmware/gv11b"
FILES_${PN}-tegra210 = "${nonarch_base_libdir}/firmware/tegra21x ${nonarch_base_libdir}/firmware/gm20b"
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
