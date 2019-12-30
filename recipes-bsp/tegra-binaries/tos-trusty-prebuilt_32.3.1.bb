require recipes-bsp/tegra-binaries/tegra-binaries-${PV}.inc
require recipes-bsp/tegra-binaries/tegra-shared-binaries.inc

COMPATIBLE_MACHINE = "(tegra186|tegra194)"
INHIBIT_DEFAULT_DEPS = "1"

TOSIMG_PREBUILT = "tos-trusty.img"
TOSIMG_PREBUILT_tegra194 = "tos-trusty_t194.img"
PREFERRED_PROVIDER_virtual/secure-os ??= ""
PROVIDES = "tos-trusty virtual/secure-os"
TOS_IMAGE ?= "tos-trusty-${MACHINE}-${PV}-${PR}.img"
TOS_SYMLINK ?= "tos-trusty-${MACHINE}.img"

inherit deploy

do_deploy() {
    install -d ${DEPLOYDIR}
    install -m 0644 ${S}/bootloader/${TOSIMG_PREBUILT} ${DEPLOYDIR}/${TOS_IMAGE}
    ln -sf ${TOS_IMAGE} ${DEPLOYDIR}/${TOS_SYMLINK}
}

PACKAGE_ARCH = "${MACHINE_ARCH}"

addtask deploy before do_build after do_install
