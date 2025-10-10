require recipes-bsp/tegra-binaries/tegra-binaries-${PV}.inc
require recipes-bsp/tegra-binaries/tegra-shared-binaries.inc

COMPATIBLE_MACHINE = "(tegra264)"
INHIBIT_DEFAULT_DEPS = "1"

PROVIDES = "hafnium"

inherit deploy

do_deploy() {
    install -D -m 0644 -t ${DEPLOYDIR} ${S}/bootloader/hafnium_t264.fip
}

PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"

addtask deploy before do_build after do_install
