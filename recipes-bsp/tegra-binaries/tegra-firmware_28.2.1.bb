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
    install -d ${D}${nonarch_base_libdir}/firmware/gm20b
    oldwd="$PWD"
    cd ${D}${nonarch_base_libdir}/firmware/gm20b
    for f in acr_ucode.bin fecs.bin fecs_sig.bin gpccs.bin gpmu_ucode.bin \
		   gpmu_ucode_desc.bin gpmu_ucode_image.bin gpu2cde.bin \
		   NETB_img.bin pmu_bl.bin pmu_sig.bin; do
	ln -s ../tegra21x/$f .
    done
    cd "$oldwd"
}

FWPREFIX_tegra186 = "tegra18x"
FWPREFIX_tegra210 = "tegra21x"
EXTRA_FIRMWARE = ""
EXTRA_FIRMWARE_tegra210 = "${nonarch_base_libdir}/firmware/gm20b"
EXTRA_FIRMWARE_tegra186 = "${nonarch_base_libdir}/firmware/gp10b"
PACKAGES = "${PN}-brcm ${PN}-xusb ${PN}"
FILES_${PN}-brcm = "${nonarch_base_libdir}/firmware/brcm ${nonarch_base_libdir}/firmware/bcm4354.hcd ${nonarch_base_libdir}/firmware/nv-BT-Version"
FILES_${PN}-xusb = "${nonarch_base_libdir}/firmware/${FWPREFIX}_xusb_firmware"
FILES_${PN} = "${nonarch_base_libdir}/firmware/${FWPREFIX} ${EXTRA_FIRMWARE}"
RDEPENDS_${PN} = "${PN}-xusb"
