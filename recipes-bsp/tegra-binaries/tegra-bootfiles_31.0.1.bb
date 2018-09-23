require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

COMPATIBLE_MACHINE = "(tegra186|tegra194)"
INHIBIT_DEFAULT_DEPS = "1"
DEPENDS = "${SOC_FAMILY}-flashtools-native dtc-native"

BCT_TEMPLATE ?= "${S}/bootloader/${NVIDIA_BOARD}/BCT/${EMMC_BCT}"
BCT_OVERRIDE_TEMPLATE ?= "${S}/bootloader/${NVIDIA_BOARD}/BCT/${EMMC_BCT_OVERRIDE}"
BOARD_CFG ?= "${S}/bootloader/${NVIDIA_BOARD}/cfg/${NVIDIA_BOARD_CFG}"
PARTITION_FILE ?= "${S}/bootloader/${NVIDIA_BOARD}/cfg/${PARTITION_LAYOUT_TEMPLATE}"
SMD_CFG ?= "${S}/bootloader/smd_info.cfg"
CBOOTOPTION_FILE ?= "${S}/bootloader/cbo.dts"

BOOTBINS_tegra186 = "\
    bmp.blob \
    bpmp.bin \
    camera-rtcpu-sce.bin \
    cboot.bin \
    eks.img \
    mb1_prod.bin \
    mb1_recovery_prod.bin \
    mce_mts_d15_prod_cr.bin \
    nvtboot_cpu.bin \
    nvtboot_recovery.bin \
    nvtboot_recovery_cpu.bin \
    preboot_d15_prod_cr.bin \
    spe.bin \
    tos.img \
"
BOOTBINS_tegra194 = "\
    bmp.blob \
    bpmp_t194.bin \
    camera-rtcpu-rce.bin \
    cboot_t194.bin \
    eks.img \
    mb1_t194_prod.bin \
    nvtboot_applet_t194.bin \
    nvtboot_t194.bin \
    preboot_c10_prod_cr.bin \
    mce_c10_prod_cr.bin \
    mts_c10_prod_cr.bin \
    nvtboot_cpu_t194.bin \
    nvtboot_recovery_t194.bin \
    nvtboot_recovery_cpu_t194.bin \
    preboot_d15_prod_cr.bin \
    spe_t194.bin \
    tos_t194.img \
    warmboot_t194_prod.bin \
"

BOOTBINS_MACHINE_SPECIFIC_tegra186 = "\
    nvtboot.bin \
    warmboot.bin \
"
BOOTBINS_MACHINE_SPECIFIC_tegra194 = ""

do_compile() {
    ${STAGING_BINDIR_NATIVE}/tegra186-flash/nv_smd_generator ${SMD_CFG} ${B}/slot_metadata.bin
}

do_compile_append_tegra194() {
    dtc -I dts -O dtb -o ${B}/cbo.dtb ${CBOOTOPTION_FILE}
}

do_install() {
    install -d ${D}${datadir}/tegraflash
    for f in ${BOOTBINS}; do
        install -m 0644 ${S}/bootloader/$f ${D}${datadir}/tegraflash
    done
    for f in ${BOOTBINS_MACHINE_SPECIFIC}; do
        install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/$f ${D}${datadir}/tegraflash
    done
    install -m 0644 ${BCT_TEMPLATE} ${D}${datadir}/tegraflash/${MACHINE}.cfg
    install -m 0644 ${PARTITION_FILE} ${D}${datadir}/tegraflash/flash_${MACHINE}.xml
    install -m 0644 ${B}/slot_metadata.bin ${D}${datadir}/tegraflash/
}

do_install_append_tegra186() {
    install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/BCT/tegra186* ${D}${datadir}/tegraflash/
    install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/tegra186-a02-bpmp*dtb ${D}${datadir}/tegraflash/
    install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/BCT/minimal_scr.cfg ${D}${datadir}/tegraflash/
    install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/BCT/mobile_scr.cfg ${D}${datadir}/tegraflash/
    install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/BCT/emmc.cfg ${D}${datadir}/tegraflash/
}

do_install_append_tegra194() {
    install -m 0644 ${BCT_OVERRIDE_TEMPLATE} ${D}${datadir}/tegraflash/${MACHINE}-override.cfg
    install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/BCT/tegra19* ${D}${datadir}/tegraflash/
    install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/tegra194-*-bpmp-*.dtb ${D}${datadir}/tegraflash/
    install -m 0644 ${S}/bootloader/xusb_sil_rel_fw ${D}${datadir}/tegraflash/
    install -m 0644 ${B}/cbo.dtb ${D}${datadir}/tegraflash/
}


PACKAGES = "${PN}-dev"
FILES_${PN}-dev = "${datadir}"
RDEPENDS_${PN}-dev = ""
PACKAGE_ARCH = "${MACHINE_ARCH}"
