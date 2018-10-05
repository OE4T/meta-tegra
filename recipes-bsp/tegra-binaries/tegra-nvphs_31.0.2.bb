require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

DESCRIPTION = "NVIDIA Power Hinting Service for TX2/Xavier"

COMPATIBLE_MACHINE = "(tegra186|tegra194)"

do_configure() {
    tar -C ${B} -x -f ${S}/nv_tegra/nvidia_drivers.tbz2 usr/sbin/nvphsd usr/lib/aarch64-linux-gnu/tegra
    tar -C ${B} -x -f ${S}/nv_tegra/config.tbz2 etc usr/sbin
}

do_compile[noexec] = "1"

do_install() {
    install -d ${D}${sbindir} ${D}${sysconfdir} ${D}${libdir}
    install -m 0755 ${B}/usr/sbin/nvphs* ${B}/usr/sbin/nvsetprop ${D}${sbindir}/
    install -m 0644 ${B}/etc/nvphsd*conf* ${D}${sysconfdir}/
    install -m 0644 ${B}/usr/lib/aarch64-linux-gnu/tegra/libnvphsd* ${D}${libdir}/
    install -d ${D}${systemd_system_unitdir}
    install -m 0644 ${B}/etc/systemd/system/nvphs.service ${D}${systemd_system_unitdir}
    sed -i -e's,/usr/sbin,${sbindir},g' ${D}${systemd_system_unitdir}/nvphs.service
}

inherit systemd

SYSTEMD_SERVICE_${PN} = "nvphs.service"

PACKAGES = "${PN}"
FILES_${PN} = "${sbindir} ${sysconfdir} ${libdir}"
RDEPENDS_${PN} = "bash tegra-libraries"
INSANE_SKIP_${PN} = "ldflags dev-so"

PACKAGE_ARCH = "${MACHINE_ARCH}"
