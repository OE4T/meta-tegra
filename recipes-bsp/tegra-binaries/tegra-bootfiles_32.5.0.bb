require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

COMPATIBLE_MACHINE = "(tegra)"
INHIBIT_DEFAULT_DEPS = "1"
DEPENDS = "${SOC_FAMILY}-flashtools-native dtc-native tegra-flashvars"

BCT_TEMPLATE ?= "${S}/bootloader/${NVIDIA_BOARD}/BCT/${EMMC_BCT}"
BCT_OVERRIDE_TEMPLATE ?= "${S}/bootloader/${NVIDIA_BOARD}/BCT/${EMMC_BCT_OVERRIDE}"
BOARD_CFG ?= "${S}/bootloader/${NVIDIA_BOARD}/cfg/${NVIDIA_BOARD_CFG}"
PARTITION_FILE ?= "${S}/bootloader/${NVIDIA_BOARD}/cfg/${PARTITION_LAYOUT_TEMPLATE}"
SMD_CFG ?= "${S}/bootloader/smd_info.cfg"
CBOOTOPTION_FILE ?= "${S}/bootloader/cbo.dts"
ODMFUSE_FILE ?= ""

BOOTBINS_tegra186 = "\
    adsp-fw.bin \
    bmp.blob \
    bpmp.bin \
    camera-rtcpu-sce.img \
    dram-ecc.bin \
    eks.img \
    mb1_prod.bin \
    mb1_recovery_prod.bin \
    mce_mts_d15_prod_cr.bin \
    nvtboot_cpu.bin \
    nvtboot_recovery.bin \
    nvtboot_recovery_cpu.bin \
    preboot_d15_prod_cr.bin \
    spe.bin \
"
BOOTBINS_tegra194 = "\
    adsp-fw.bin \
    bmp.blob \
    bpmp_t194.bin \
    camera-rtcpu-rce.img \
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
    warmboot_t194_prod.bin \
"

BOOTBINS_tegra210 = "\
    bmp.blob \
    eks.img \
    nvtboot_cpu.bin \
    nvtboot_recovery.bin \
    nvtboot_recovery_cpu.bin \
    rp4.blob \
    tos-mon-only.img \
"

BOOTBINS_MACHINE_SPECIFIC_tegra186 = "\
    nvtboot.bin \
    warmboot.bin \
"
BOOTBINS_MACHINE_SPECIFIC_tegra194 = ""

BOOTBINS_MACHINE_SPECIFIC_tegra210 = "\
    nvtboot.bin \
    sc7entry-firmware.bin \
    warmboot.bin \
"
do_compile() {
    :
}

do_compile_append_tegra186() {
    ${STAGING_BINDIR_NATIVE}/tegra186-flash/nv_smd_generator ${SMD_CFG} ${B}/slot_metadata.bin
}

do_compile_append_tegra194() {
    ${STAGING_BINDIR_NATIVE}/tegra186-flash/nv_smd_generator ${SMD_CFG} ${B}/slot_metadata.bin
    dtc -I dts -O dtb -o ${B}/cbo.dtb ${CBOOTOPTION_FILE}
}

do_install() {
    install -d ${D}${datadir}/tegraflash
    install -m 0644 ${S}/nv_tegra/bsp_version ${D}${datadir}/tegraflash/
    for f in ${BOOTBINS}; do
        install -m 0644 ${S}/bootloader/$f ${D}${datadir}/tegraflash
    done
    for f in ${BOOTBINS_MACHINE_SPECIFIC}; do
        install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/$f ${D}${datadir}/tegraflash
    done
    install -m 0644 ${BCT_TEMPLATE} ${D}${datadir}/tegraflash/${MACHINE}.cfg
    install -m 0644 ${PARTITION_FILE} ${D}${datadir}/tegraflash/${PARTITION_LAYOUT_TEMPLATE}
    [ -z "${ODMFUSE_FILE}" ] || install -m 0644 ${ODMFUSE_FILE} ${D}${datadir}/tegraflash/odmfuse_pkc_${MACHINE}.xml
}

do_install_append_tegra186() {
    install -m 0644 ${B}/slot_metadata.bin ${D}${datadir}/tegraflash/
    install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/BCT/tegra186* ${D}${datadir}/tegraflash/
    install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/tegra186-a02-bpmp*dtb ${D}${datadir}/tegraflash/
    install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/BCT/minimal_scr.cfg ${D}${datadir}/tegraflash/
    install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/BCT/mobile_scr.cfg ${D}${datadir}/tegraflash/
    install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/BCT/emmc.cfg ${D}${datadir}/tegraflash/
}

do_install_append_tegra194() {
    install -m 0644 ${B}/slot_metadata.bin ${D}${datadir}/tegraflash/
    install -m 0644 ${BCT_OVERRIDE_TEMPLATE} ${D}${datadir}/tegraflash/${MACHINE}-override.cfg
    install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/BCT/tegra19* ${D}${datadir}/tegraflash/
    install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/tegra194-*-bpmp-*.dtb ${D}${datadir}/tegraflash/
    install -m 0644 ${S}/bootloader/xusb_sil_rel_fw ${D}${datadir}/tegraflash/
    install -m 0644 ${B}/cbo.dtb ${D}${datadir}/tegraflash/
}

do_install_append_tegra210() {
    [ -z "${NVIDIA_BOARD_CFG}" ] || install -m 0644 ${BOARD_CFG} ${D}${datadir}/tegraflash/board_config_${MACHINE}.xml
}

PACKAGES = "${PN}-dev"
FILES_${PN}-dev = "${datadir}"
RDEPENDS_${PN}-dev = ""
PACKAGE_ARCH = "${MACHINE_ARCH}"
