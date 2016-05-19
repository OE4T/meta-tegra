require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

COMPATIBLE_MACHINE = "(jetson-tx1)"
PACKAGE_ARCH = "${MACHINE_ARCH}"

do_configure() {
    tar -C ${B} -x -f ${S}/nv_tegra/nvidia_drivers.tbz2 lib/firmware
}

do_compile[noexec] = "1"

do_install() {
    install -d ${D}/lib
    cp -R -f ${B}/lib/firmware ${D}/lib/
}

PACKAGES = "${PN}-brcm ${PN}"
FILES_${PN}-brcm = "/lib/firmware/brcm /lib/firmware/bcm4354.hcd"
FILES_${PN} = "/lib/firmware/tegra21x*"
