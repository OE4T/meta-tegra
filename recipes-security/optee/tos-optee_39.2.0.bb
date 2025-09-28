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
BL31_IMAGE ?= "bl31-${MACHINE}-${PV}-${PR}.fip"
TOS_SYMLINK ?= "tos-${MACHINE}.img"
BL31_SYMLINK ?= "bl31-${MACHINE}.fip"

inherit deploy nopackages

DEPENDS = "tegra-flashtools-native dtc-native optee-os arm-trusted-firmware"
DEPENDS:append:tegra264 = " l4t-atf-tools-native"

S = "${UNPACKDIR}/optee"

do_configure[noexec] = "1"

gen_tos_image() {
    bbfatal "Unknown platform"
}

gen_tos_image:tegra234() {
    ${PYTHON} ${STAGING_BINDIR_NATIVE}/tegra-flash/gen_tos_part_img.py --monitor ${STAGING_DATADIR}/trusted-os/bl31.bin \
      --os ${STAGING_DATADIR}/trusted-os/tee-raw.bin \
      --dtb ${S}/${SOC_FAMILY}-optee.dtb \
      --tostype optee ${TOSIMG}
}

gen_tos_image:tegra264() {
    ${PYTHON} ${STAGING_BINDIR_NATIVE}/sptool.py \
        -i ${STAGING_DATADIR}/trusted-os/tee-raw.bin:${S}/${SOC_FAMILY}-optee.dtb \
        -o ${B}/${TOSIMG}
}

do_compile() {
    dtc -I dts -O dtb -o ${S}/${SOC_FAMILY}-optee.dtb ${S}/${SOC_FAMILY}-optee.dts
    gen_tos_image
} 

do_install[noexec] = "1"

deploy_atf() {
    :
}
deploy_atf:tegra264() {
    install -m 0644 ${STAGING_DATADIR}/trusted-os/bl31.fip ${DEPLOYDIR}/${BL31_IMAGE}
    ln -sf ${BL31_IMAGE} ${DEPLOYDIR}/${BL31_SYMLINK}
}

do_deploy() {
    install -d ${DEPLOYDIR}
    install -m 0644 ${S}/${TOSIMG} ${DEPLOYDIR}/${TOS_IMAGE}
    ln -sf ${TOS_IMAGE} ${DEPLOYDIR}/${TOS_SYMLINK}
    deploy_atf
}

TOS_DEPLOY_DEPS = ""
TOS_DEPLOY_DEPS:tegra264 = "edk2-nvidia-standalone-mm:do_deploy hafnium:do_deploy"
do_deploy[depends] += "${TOS_DEPLOY_DEPS}"

addtask deploy before do_build after do_compile

PACKAGE_ARCH = "${MACHINE_ARCH}"
