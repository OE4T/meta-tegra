require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

COMPATIBLE_MACHINE = "(tegra)"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"

inherit systemd

do_configure() {
	tar -C ${B} -x -f ${S}/nv_tegra/nv_tools.tbz2 usr/sbin
        tar -C ${B} -x -f ${S}/nv_tegra/config.tbz2 etc
}

do_compile() {
	:
}

do_install() {
	install -d ${D}${sbindir}
	install -m 0755 ${B}/usr/sbin/nvbootctrl ${D}${sbindir}
	install -m 0755 ${B}/usr/sbin/nv_bootloader_payload_updater ${D}${sbindir}
	install -m 0755 ${B}/usr/sbin/nv_update_engine ${D}${sbindir}
	install -d ${D}${systemd_system_unitdir}
	install -m 0644 ${B}/etc/systemd/system/nv_update_verifier.service ${D}${systemd_system_unitdir}
	sed -i -e's,^After=nv\.service,After=nvstartup.service,' ${D}${systemd_system_unitdir}/nv_update_verifier.service
	install -d ${D}/opt/ota_package
}

do_install_append_tegra186() {
	install -d ${D}${datadir}/nv_tegra/rollback/t18x
	install -m 0644 ${S}/bootloader/rollback/t18x/rollback.cfg ${D}${datadir}/nv_tegra/rollback/t18x/
}

do_install_append_tegra194() {
	install -d ${D}${datadir}/nv_tegra/rollback/t19x
	install -m 0644 ${S}/bootloader/rollback/t19x/rollback.cfg ${D}${datadir}/nv_tegra/rollback/t19x/
}

do_install_tegra210() {
	install -d ${D}${sbindir}
	install -m 0755 ${B}/usr/sbin/l4t_payload_updater_t210 ${D}${sbindir}
	sed -i -e's,^#!/usr/bin/python,#!/usr/bin/env python2,' ${D}${sbindir}/l4t_payload_updater_t210
	install -d ${D}/opt/ota_package
}

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"
PACKAGES = "${PN}-nvbootctrl ${PN} ${PN}-dev"
FILES_${PN}-nvbootctrl = "${sbindir}/nvbootctrl"
FILES_${PN} += "/opt/ota_package"
FILES_${PN}-dev = "${datadir}/nv_tegra/rollback"
SYSTEMD_SERVICE_${PN} = "nv_update_verifier.service"
SYSTEMD_SERVICE_${PN}_tegra210 = ""
RDEPENDS_${PN}-nvbootctrl = "setup-nv-boot-control"
RDEPENDS_${PN}-nvbootctrl_tegra210 = ""
ALLOW_EMPTY_${PN}-nvbootctrl_tegra210 = "1"
RDEPENDS_${PN} = "${PN}-nvbootctrl setup-nv-boot-control-service"
RDEPENDS_${PN}_tegra210 = "setup-nv-boot-control-service python-argparse python-core python-subprocess python-textutils python-shell"
INSANE_SKIP_${PN} = "ldflags"
INSANE_SKIP_${PN}-nvbootctrl = "ldflags"
