require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

DESCRIPTION = "NVIDIA Power Hinting Service"

inherit container-runtime-csv

COMPATIBLE_MACHINE = "(tegra)"
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
}

PACKAGES = "${PN}"
FILES_${PN} = "${sbindir} ${sysconfdir} ${libdir}"
RDEPENDS_${PN} = "bash tegra-libraries"
INSANE_SKIP_${PN} = "ldflags dev-so"
INHIBIT_SYSROOT_STRIP = "1"
INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"

PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
