require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

DESCRIPTION = "NVIDIA sensor HAL daemon"

COMPATIBLE_MACHINE = "(tegra)"
COMPATIBLE_MACHINE_tegra124 = "(-)"

do_configure() {
    tar -C ${B} -x -f ${S}/nv_tegra/nvidia_drivers.tbz2 usr/sbin/nvs-service
}

do_compile[noexec] = "1"

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${B}/usr/sbin/nvs-service ${D}${sbindir}/
    install -d ${D}${systemd_system_unitdir} ${D}${sysconfdir}/init.d
    install -m 0644 ${S}/nvs-service.service ${D}${systemd_system_unitdir}
    install -m 0755 ${S}/nvs-service.init ${D}${sysconfdir}/init.d/nvs-service
    sed -i -e's,/usr/sbin,${sbindir},g' ${D}${systemd_system_unitdir}/nvs-service.service
}

inherit systemd update-rc.d

INITSCRIPT_NAME = "nvs-service"
INITSCRIPT_PARAMS = "defaults"
SYSTEMD_SERVICE_${PN} = "nvs-service.service"

PACKAGES = "${PN}"
FILES_${PN} = "${sbindir} ${sysconfdir}"
RDEPENDS_${PN} = "bash tegra-libraries"
INSANE_SKIP_${PN} = "ldflags"

PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
