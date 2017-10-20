require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

do_configure() {
    tar -C ${B} -x -f ${S}/nv_tegra/nvidia_drivers.tbz2 lib/firmware
}

do_compile[noexec] = "1"

do_install() {
    install -d ${D}/lib
    cp -R -f ${B}/lib/firmware ${D}/lib/
}

PACKAGES = "${PN}-brcm ${PN}-xusb ${PN}"
FILES_${PN}-brcm = "/lib/firmware/brcm /lib/firmware/bcm4354.hcd /lib/firmware/nv-BT-Version"
FILES_${PN}-xusb = "/lib/firmware/tegra21x_xusb_firmware"
FILES_${PN} = "/lib/firmware/tegra21x"
RDEPENDS_${PN} = "${PN}-xusb"
