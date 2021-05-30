require recipes-bsp/tegra-binaries/tegra-binaries-${PV}.inc
require recipes-bsp/tegra-binaries/tegra-shared-binaries.inc

COMPATIBLE_MACHINE = "(tegra194)"
INHIBIT_DEFAULT_DEPS = "1"

PREFERRED_PROVIDER_virtual/bootloader ??= ""
PROVIDES = "jetson-uefi"
PROVIDES += "${@'virtual/bootloader' if d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('jetson-uefi') else ''}"
UEFI_IMAGE ?= "uefi_jetson-${MACHINE}-${PV}-${PR}.bin"
UEFI_SYMLINK ?= "uefi_jetson-${MACHINE}.bin"
UEFIVARS_IMAGE ?= "uefi_jetson_variables-${MACHINE}-${PV}-${PR}.bin"
UEFIVARS_SYMLINK ?= "uefi_jetson_variables-${MACHINE}.bin"

inherit deploy

do_deploy() {
    install -d ${DEPLOYDIR}
    install -m 0644 ${S}/bootloader/uefi_jetson.bin ${DEPLOYDIR}/${UEFI_IMAGE}
    install -m 0644 ${S}/bootloader/uefi_jetson_variables.bin ${DEPLOYDIR}/${UEFIVARS_IMAGE}
    ln -sf ${UEFI_IMAGE} ${DEPLOYDIR}/${UEFI_SYMLINK}
    ln -sf ${UEFIVARS_IMAGE} ${DEPLOYDIR}/${UEFIVARS_SYMLINK}
}

PACKAGE_ARCH = "${MACHINE_ARCH}"

addtask deploy before do_build after do_install
