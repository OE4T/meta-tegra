DESCRIPTION = "Construct a trusted OS image with ATF and OP-TEE"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

require optee-l4t.inc

# We only need the device tree file for this recipe
TEGRA_SRC_SUBARCHIVE_OPTS = "optee/${SOC_FAMILY}-optee.dts"

TOSIMG ?= "tos-optee_${OPTEE_NV_PLATFORM}.img"
PREFERRED_PROVIDER_virtual/secure-os ??= ""
PROVIDES += "trusted-os virtual/secure-os"
TOS_IMAGE ?= "tos-${MACHINE}-${PV}-${PR}.img"
TOS_SYMLINK ?= "tos-${MACHINE}.img"

inherit deploy nopackages

DEPENDS = "tegra-flashtools-native dtc-native optee-os arm-trusted-firmware"

S = "${WORKDIR}/optee"

do_configure[noexec] = "1"

do_compile() {
    dtc -I dts -O dtb -o ${S}/${SOC_FAMILY}-optee.dtb ${S}/${SOC_FAMILY}-optee.dts

    ${PYTHON} ${STAGING_BINDIR_NATIVE}/tegra-flash/gen_tos_part_img.py --monitor ${STAGING_DATADIR}/trusted-os/bl31.bin \
      --os ${STAGING_DATADIR}/trusted-os/tee-raw.bin \
      --dtb ${S}/${SOC_FAMILY}-optee.dtb \
      --tostype optee ${TOSIMG}
} 

do_install[noexec] = "1"

do_deploy() {
    install -d ${DEPLOYDIR}
    install -m 0644 ${S}/${TOSIMG} ${DEPLOYDIR}/${TOS_IMAGE}
    ln -sf ${TOS_IMAGE} ${DEPLOYDIR}/${TOS_SYMLINK}
}

addtask deploy before do_build after do_compile

PACKAGE_ARCH = "${MACHINE_ARCH}"
