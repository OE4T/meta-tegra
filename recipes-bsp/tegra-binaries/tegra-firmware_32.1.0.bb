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

PACKAGES = "${PN}-brcm ${PN}-tegra186-xusb ${PN}-tegra194-xusb ${PN}-tegra186 ${PN}-tegra194 ${PN}-xusb ${PN}"
FILES_${PN}-brcm = "${nonarch_base_libdir}/firmware/brcm ${nonarch_base_libdir}/firmware/bcm4354.hcd ${nonarch_base_libdir}/firmware/nv-BT-Version"
FILES_${PN}-tegra186-xusb = "${nonarch_base_libdir}/firmware/tegra18x_xusb_firmware"
FILES_${PN}-tegra194-xusb = "${nonarch_base_libdir}/firmware/tegra19x_xusb_firmware"
FILES_${PN}-tegra186 = "${nonarch_base_libdir}/firmware/tegra18x ${nonarch_base_libdir}/firmware/gp10b"
FILES_${PN}-tegra194 = "${nonarch_base_libdir}/firmware/tegra19x ${nonarch_base_libdir}/firmware/gv11b"
FILES_${PN}-xusb = ""
ALLOW_EMPTY_${PN}-xusb = "1"
FILES_${PN} = ""
ALLOW_EMPTY_${PN} = "1"
XUSBDEPS = ""
XUSBDEPS_tegra186 = "${PN}-tegra186-xusb"
XUSBDEPS_tegra194 = "${PN}-tegra194-xusb"
RDEPENDS_${PN}-xusb = "${XUSBDEPS}"
FWDEPS = ""
FWDEPS_tegra186 = "${PN}-tegra186"
FWDEPS_tegra194 = "${PN}-tegra194"
RDEPENDS_${PN} = "${FWDEPS} ${PN}-xusb"
