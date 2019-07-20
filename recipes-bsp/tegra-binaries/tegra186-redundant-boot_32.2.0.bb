require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

COMPATIBLE_MACHINE = "(tegra186|tegra194)"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"

TNSPEC = "XXXX-XXX-fuselevel_unspecified"
TNSPEC_tegra186 ?= "${TEGRA_BOARDID}-${TEGRA_FAB}-fuselevel_production"
TNSPEC_tegra194 ?= "${TEGRA_BOARDID}-${TEGRA_FAB}-fuselevel_production"

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
	install -d ${D}${sysconfdir}
	sed -e 's,^TNSPEC.*$,TNSPEC ${TNSPEC},' \
	    -e '/^TEGRA_CHIPID/d' \
	    -e '$ a TEGRA_CHIPID ${NVIDIA_CHIP}' ${B}/etc/nv_boot_control.conf >${D}${sysconfdir}/nv_boot_control.conf
	chmod 0644 ${D}${sysconfdir}/nv_boot_control.conf
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

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_PACKAGE_DEBUG_SPLIT = "1"
INHIBIT_SYSROOT_STRIP = "1"
PACKAGES = "${PN} ${PN}-dev"
FILES_${PN} += "/opt/ota_package"
FILES_${PN}-dev = "${datadir}/nv_tegra/rollback"
SYSTEMD_SERVICE_${PN} = "nv_update_verifier.service"
INSANE_SKIP_${PN} = "ldflags"
