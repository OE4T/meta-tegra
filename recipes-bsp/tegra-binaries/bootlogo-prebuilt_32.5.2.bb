require recipes-bsp/tegra-binaries/tegra-binaries-${PV}.inc
require recipes-bsp/tegra-binaries/tegra-shared-binaries.inc

COMPATIBLE_MACHINE = "(tegra)"
INHIBIT_DEFAULT_DEPS = "1"

BMPBLOB_PREBUILT = "bmp.blob"
PREFERRED_PROVIDER_virtual/bootlogo ??= ""
PROVIDES += "virtual/bootlogo"
BMP_BLOB ?= "bootlogo-${MACHINE}-${PV}-${PR}.blob"
BMP_SYMLINK ?= "bootlogo-${MACHINE}.blob"

inherit deploy

do_deploy() {
    install -d ${DEPLOYDIR}
    install -m 0644 ${S}/bootloader/${BMPBLOB_PREBUILT} ${DEPLOYDIR}/${BMP_BLOB}
    ln -sf ${BMP_BLOB} ${DEPLOYDIR}/${BMP_SYMLINK}
}

PACKAGE_ARCH = "${MACHINE_ARCH}"

addtask deploy before do_build after do_install
