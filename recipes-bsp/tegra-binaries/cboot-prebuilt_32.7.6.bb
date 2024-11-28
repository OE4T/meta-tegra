require recipes-bsp/tegra-binaries/tegra-binaries-${PV}.inc
require recipes-bsp/tegra-binaries/tegra-shared-binaries.inc

COMPATIBLE_MACHINE = "(tegra)"
INHIBIT_DEFAULT_DEPS = "1"

CBOOTBIN_PREBUILT = "cboot.bin"
CBOOTBIN_PREBUILT:tegra194 = "cboot_t194.bin"
CBOOTBIN_PREBUILT:tegra210 = "${@'t210ref/cboot_rb.bin' if d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('cboot') else 't210ref/cboot.bin'}"
PREFERRED_PROVIDER_virtual/bootloader ??= ""
PROVIDES = "cboot"
PROVIDES += "${@'virtual/bootloader' if d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('cboot') else ''}"
CBOOT_IMAGE ?= "cboot-${MACHINE}-${PV}-${PR}.bin"
CBOOT_SYMLINK ?= "cboot-${MACHINE}.bin"

inherit deploy

do_deploy() {
    install -d ${DEPLOYDIR}
    install -m 0644 ${S}/bootloader/${CBOOTBIN_PREBUILT} ${DEPLOYDIR}/${CBOOT_IMAGE}
    ln -sf ${CBOOT_IMAGE} ${DEPLOYDIR}/${CBOOT_SYMLINK}
}

PACKAGE_ARCH = "${MACHINE_ARCH}"

addtask deploy before do_build after do_install
