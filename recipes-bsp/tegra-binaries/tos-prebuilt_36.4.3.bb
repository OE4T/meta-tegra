require recipes-bsp/tegra-binaries/tegra-binaries-${PV}.inc
require recipes-bsp/tegra-binaries/tegra-shared-binaries.inc

COMPATIBLE_MACHINE = "(tegra)"
INHIBIT_DEFAULT_DEPS = "1"

TOSIMG_PREBUILT = "tos-trusty.img"
TOSIMG_PREBUILT:tegra234 = "tos-optee_t234.img"
PREFERRED_PROVIDER_virtual/secure-os ??= ""
PROVIDES = "trusted-os virtual/secure-os"
TOS_IMAGE ?= "tos-${MACHINE}-${PV}-${PR}.img"
TOS_SYMLINK ?= "tos-${MACHINE}.img"

inherit deploy

do_deploy() {
    install -d ${DEPLOYDIR}
    install -m 0644 ${S}/bootloader/${TOSIMG_PREBUILT} ${DEPLOYDIR}/${TOS_IMAGE}
    ln -sf ${TOS_IMAGE} ${DEPLOYDIR}/${TOS_SYMLINK}
}

PACKAGE_ARCH = "${MACHINE_ARCH}"

addtask deploy before do_build after do_install
