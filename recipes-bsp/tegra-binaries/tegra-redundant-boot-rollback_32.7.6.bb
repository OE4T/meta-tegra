require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

COMPATIBLE_MACHINE = "(tegra)"
PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
TEGRA_BYPASS_TNSPEC_CHECK ??= "0"

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

do_install:append:tegra186() {
	install -d ${D}${datadir}/nv_tegra/rollback/t18x
	install -m 0644 ${S}/bootloader/rollback/t18x/rollback.cfg ${D}${datadir}/nv_tegra/rollback/t18x/
}

do_install:append:tegra194() {
	install -d ${D}${datadir}/nv_tegra/rollback/t19x
	install -m 0644 ${S}/bootloader/rollback/t19x/rollback.cfg ${D}${datadir}/nv_tegra/rollback/t19x/
}

python () {
    if bb.utils.to_boolean(d.getVar('TEGRA_BYPASS_TNSPEC_CHECK'), False):
        return
    machine = d.getVar('MACHINE')
    if machine and len(machine) > 31:
        bb.warn('MACHINE name must be less than 32 characters for bootloader update payload generation')
}
