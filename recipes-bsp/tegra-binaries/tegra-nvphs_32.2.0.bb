require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

DESCRIPTION = "NVIDIA Power Hinting Service"

COMPATIBLE_MACHINE = "(tegra)"
COMPATIBLE_MACHINE_tegra124 = "(-)"
NVPHSD_MACHINE_CONF = "nvphsd.conf"
NVPHSD_MACHINE_CONF_tegra186 = "nvphsd.conf.t186"
NVPHSD_MACHINE_CONF_tegra194 = "nvphsd.conf.t194"

do_configure() {
    tar -C ${B} -x -f ${S}/nv_tegra/nvidia_drivers.tbz2 usr/sbin/nvphsd usr/lib/aarch64-linux-gnu/tegra
    tar -C ${B} -x -f ${S}/nv_tegra/config.tbz2 etc usr/sbin
}

do_compile[noexec] = "1"

do_install() {
    install -d ${D}${sbindir} ${D}${sysconfdir} ${D}${libdir}
    install -m 0755 ${B}/usr/sbin/nvphs* ${B}/usr/sbin/nvsetprop ${D}${sbindir}/
    install -m 0644 ${B}/etc/nvphsd_common.conf ${D}${sysconfdir}/
    install -m 0644 ${B}/etc/${NVPHSD_MACHINE_CONF} ${D}${sysconfdir}/nvphsd.conf
    install -m 0644 ${B}/usr/lib/aarch64-linux-gnu/tegra/libnvphsd* ${D}${libdir}/
    install -m 0644 ${B}/usr/lib/aarch64-linux-gnu/tegra/libnvgov* ${D}${libdir}/
    install -d ${D}${systemd_system_unitdir} ${D}${sysconfdir}/init.d
    install -m 0644 ${S}/nvphs.service ${D}${systemd_system_unitdir}
    install -m 0755 ${S}/nvphs.init ${D}${sysconfdir}/init.d/nvphs
    sed -i -e's,/usr/sbin,${sbindir},g' ${D}${systemd_system_unitdir}/nvphs.service
}

inherit systemd update-rc.d

INITSCRIPT_NAME = "nvphs"
INITSCRIPT_PARAMS = "defaults"
SYSTEMD_SERVICE_${PN} = "nvphs.service"

PACKAGES = "${PN}"
FILES_${PN} = "${sbindir} ${sysconfdir} ${libdir}"
RDEPENDS_${PN} = "bash tegra-libraries"
INSANE_SKIP_${PN} = "ldflags dev-so"

PACKAGE_ARCH = "${MACHINE_ARCH}"
