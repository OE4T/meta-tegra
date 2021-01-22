require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

COMPATIBLE_MACHINE = "(tegra)"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"

inherit nopackages

do_configure() {
	:
}
do_compile() {
	:
}

do_install() {
	:
}

do_install_append_tegra186() {
	install -d ${D}${datadir}/nv_tegra/rollback/t18x
	install -m 0644 ${S}/bootloader/rollback/t18x/rollback.cfg ${D}${datadir}/nv_tegra/rollback/t18x/
}

do_install_append_tegra194() {
	install -d ${D}${datadir}/nv_tegra/rollback/t19x
	install -m 0644 ${S}/bootloader/rollback/t19x/rollback.cfg ${D}${datadir}/nv_tegra/rollback/t19x/
}
