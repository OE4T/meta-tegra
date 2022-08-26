require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

COMPATIBLE_MACHINE = "(tegra)"
INHIBIT_DEFAULT_DEPS = "1"
DEPENDS = "tegra-flashtools-native dtc-native tegra-flashvars lz4-native coreutils-native"

BCT_TEMPLATE ?= "${S}/bootloader/${NVIDIA_BOARD}/BCT/${EMMC_BCT}"
BCT_OVERRIDE_TEMPLATE ?= "${S}/bootloader/${NVIDIA_BOARD}/BCT/${EMMC_BCT_OVERRIDE}"
BOARD_CFG ?= "${S}/bootloader/${NVIDIA_BOARD}/cfg/${NVIDIA_BOARD_CFG}"
PARTITION_FILE ?= "${S}/bootloader/${NVIDIA_BOARD}/cfg/${PARTITION_LAYOUT_TEMPLATE}"
ODMFUSE_FILE ?= ""

BOOTBINS:tegra194 = "\
    adsp-fw.bin \
    bpmp-2_t194.bin \
    camera-rtcpu-t194-rce.img \
    dram-ecc-t194.bin \
    eks.img \
    mb1_t194_prod.bin \
    nvdisp-init.bin \
    nvtboot_applet_t194.bin \
    nvtboot_t194.bin \
    preboot_c10_prod_cr.bin \
    mce_c10_prod_cr.bin \
    mts_c10_prod_cr.bin \
    nvtboot_cpu_t194.bin \
    nvtboot_recovery_t194.bin \
    nvtboot_recovery_cpu_t194.bin \
    spe_t194.bin \
    warmboot_t194_prod.bin \
"

BOOTBINS:tegra234 = "\
    adsp-fw.bin \
    applet_t234.bin \
    ${BPF_FILE} \
    camera-rtcpu-t234-rce.img \
    eks.img \
    mb1_t234_prod.bin \
    mb2_t234.bin \
    mb2rf_t234.bin \
    preboot_c10_prod_cr.bin \
    mce_c10_prod_cr.bin \
    mts_c10_prod_cr.bin \
    nvtboot_cpurf_t234.bin \
    spe_t234.bin \
    psc_bl1_t234_prod.bin \
    pscfw_t234_prod.bin \
    mce_flash_o10_cr_prod.bin \
    sc7_t234_prod.bin \
    display-t234-dce.bin \
    psc_rf_t234_prod.bin \
    nvdec_t234_prod.fw \
    xusb_t234_prod.bin \
    tegrabl_carveout_id.h \
    pinctrl-tegra.h \
    tegra234-gpio.h \
    gpio.h \
    readinfo_t234_min_prod.xml \
    camera-rtcpu-sce.img \
"

BOOTBINS_MACHINE_SPECIFIC:tegra194 = ""
BOOTBINS_MACHINE_SPECIFIC:tegra234 = ""

do_compile() {
    :
}

do_compile:append:tegra194() {
    for f in ${S}/bootloader/${NVIDIA_BOARD}/tegra194-*-bpmp-*.dtb; do
        compressedfile=${B}/$(basename "$f" .dtb)_lz4.dtb
        lz4c -f $f $compressedfile
    done
    cp ${S}/bootloader/nvdisp-init.bin ${B}
    truncate --size=393216 ${B}/nvdisp-init.bin
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
    install -m 0644 ${PARTITION_FILE} ${D}${datadir}/tegraflash/${PARTITION_LAYOUT_TEMPLATE}
    [ -z "${ODMFUSE_FILE}" ] || install -m 0644 ${ODMFUSE_FILE} ${D}${datadir}/tegraflash/odmfuse_pkc_${MACHINE}.xml
    install -m 0644 ${BCT_TEMPLATE} ${D}${datadir}/tegraflash/${EMMC_BCT}
    install -m 0644 ${S}/bootloader/xusb_sil_rel_fw ${D}${datadir}/tegraflash/
}

do_install:append:tegra194() {
    install -m 0644 ${B}/nvdisp-init.bin ${D}${datadir}/tegraflash/
    install -m 0644 ${BCT_OVERRIDE_TEMPLATE} ${D}${datadir}/tegraflash/${EMMC_BCT_OVERRIDE}
    install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/BCT/tegra19* ${D}${datadir}/tegraflash/
    for f in ${S}/bootloader/${NVIDIA_BOARD}/tegra194-*-bpmp-*.dtb; do
        install -m 0644 $f ${D}${datadir}/tegraflash/
        compressedfile=${B}/$(basename "$f" .dtb)_lz4.dtb
	install -m 0644 $compressedfile ${D}${datadir}/tegraflash/
    done
}

do_install:append:tegra234() {
    install -m 0644 ${S}/bootloader/tegra234-*.dts* ${D}${datadir}/tegraflash/
    install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/tegra234-bpmp-*.dtb ${D}${datadir}/tegraflash/
    install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/BCT/tegra234* ${D}${datadir}/tegraflash/
}

PACKAGES = "${PN}-dev"
FILES:${PN}-dev = "${datadir}"
RRECOMMENDS:${PN}-dev = ""
PACKAGE_ARCH = "${MACHINE_ARCH}"
