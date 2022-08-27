DESCRIPTION = "Trusted OS image"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

TOSIMG = "UNKNOWN"
TOSIMG:tegra194 = "tos-optee_t194.img"
TOSIMG:tegra234 = "tos-optee_t234.img"
PREFERRED_PROVIDER_virtual/secure-os ??= ""
PROVIDES = "trusted-os virtual/secure-os"
TOS_IMAGE ?= "tos-${MACHINE}-${PV}-${PR}.img"
TOS_SYMLINK ?= "tos-${MACHINE}.img"

inherit deploy python3native
require optee-tegra.inc

DEPENDS = "tegra-flashtools-native dtc-native optee-os-tegra arm-trusted-firmware"

S = "${WORKDIR}/optee"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    if [ -e "${S}/${SOC_FAMILY}-optee.dts" ]; then
        dtc -I dts -O dtb -o ${S}/${SOC_FAMILY}-optee.dtb ${S}/${SOC_FAMILY}-optee.dts
    else
        bberror "Device tree ${SOC_FAMILY}-optee.dts is missing."
    fi

    ${PYTHON} ${STAGING_BINDIR_NATIVE}/tegra-flash/gen_tos_part_img.py --monitor ${STAGING_DATADIR}/trusted-os/bl31.bin \
      --os ${STAGING_DATADIR}/trusted-os/tee-raw.bin \
      --dtb ${S}/${SOC_FAMILY}-optee.dtb \
      --tostype optee ${TOSIMG}
} 

do_deploy() {
    install -d ${DEPLOYDIR}
    install -m 0644 ${S}/${TOSIMG} ${DEPLOYDIR}/${TOS_IMAGE}
    ln -sf ${TOS_IMAGE} ${DEPLOYDIR}/${TOS_SYMLINK}
}

addtask deploy before do_build after do_install

PACKAGE_ARCH = "${MACHINE_ARCH}"
