require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

DESCRIPTION = "NVIDIA sensor HAL daemon"

COMPATIBLE_MACHINE = "(tegra)"

do_configure() {
    tar -C ${B} -x -f ${S}/nv_tegra/nvidia_drivers.tbz2 usr/sbin/nvs-service
}

do_compile[noexec] = "1"

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${B}/usr/sbin/nvs-service ${D}${sbindir}/
}

PACKAGES = "${PN}"
FILES_${PN} = "${sbindir} ${sysconfdir}"
RDEPENDS_${PN} = "bash tegra-libraries"
INSANE_SKIP_${PN} = "ldflags"

PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
