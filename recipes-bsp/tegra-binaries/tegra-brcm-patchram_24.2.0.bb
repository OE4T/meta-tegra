require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

do_configure() {
    tar -C ${B} -x -f ${S}/nv_tegra/nvidia_drivers.tbz2 usr/sbin
}

do_compile[noexec] = "1"

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${B}/usr/sbin/brcm_patchram_plus ${D}${sbindir}
}

PACKAGES = "${PN}"
FILES_${PN}-brcm-patchram = "${sbindir}/brcm_patchram_plus"
