require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

COMPATIBLE_MACHINE = "(tegra)"
INHIBIT_DEFAULT_DEPS = "1"
DEPENDS = "${SOC_FAMILY}-flashtools-native dtc-native tegra-flashvars lz4-native"

FLASHTOOLS_DIR = "${SOC_FAMILY}-flash"
FLASHTOOLS_DIR:tegra194 = "tegra186-flash"

BCT_TEMPLATE ?= "${S}/bootloader/${NVIDIA_BOARD}/BCT/${EMMC_BCT}"
BCT_OVERRIDE_TEMPLATE ?= "${S}/bootloader/${NVIDIA_BOARD}/BCT/${EMMC_BCT_OVERRIDE}"
BOARD_CFG ?= "${S}/bootloader/${NVIDIA_BOARD}/cfg/${NVIDIA_BOARD_CFG}"
PARTITION_FILE ?= "${S}/bootloader/${NVIDIA_BOARD}/cfg/${PARTITION_LAYOUT_TEMPLATE}"
PARTITION_FILE_EXTERNAL ?= "${DEFAULT_PARTITION_FILE_EXTERNAL}"
DEFAULT_PARTITION_FILE_EXTERNAL = "${S}/tools/kernel_flash/${PARTITION_LAYOUT_EXTERNAL}"
DEFAULT_PARTITION_FILE_EXTERNAL:tegra210 = ""
PARTITION_FILE_EXTERNAL_FALLBACK = ""
PARTITION_FILE_EXTERNAL_FALLBACK:tegra210 = "${S}/bootloader/${NVIDIA_BOARD}/cfg/flash_l4t_t210_max-spi_sd_p3448.xml"
EXTRA_XML_SPLIT_ARGS = "--change-device-type=sdcard"
EXTRA_XML_SPLIT_ARGS:tegra186 = "--change-device-type=nvme --retain-app-boot-partition"
FILTER_EXTERNAL_PARTITIONS = "false"
FILTER_EXTERNAL_PARTITIONS:tegra186 = "true"
SMD_CFG ?= "${S}/bootloader/smd_info.cfg"
CBOOTOPTION_FILE ?= ""
ODMFUSE_FILE ?= ""

BOOTBINS:tegra186 = "\
    adsp-fw.bin \
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
BOOTBINS:tegra194 = "\
    adsp-fw.bin \
    bpmp_t194.bin \
    camera-rtcpu-rce.img \
    dram-ecc-t194.bin \
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

BOOTBINS:tegra210 = "\
    eks.img \
    nvtboot_cpu.bin \
    nvtboot_cpu_rb.bin \
    nvtboot_recovery.bin \
    nvtboot_recovery_cpu.bin \
    rp4.blob \
    tos-mon-only.img \
"

BOOTBINS_MACHINE_SPECIFIC:tegra186 = "\
    nvtboot.bin \
    warmboot.bin \
"
BOOTBINS_MACHINE_SPECIFIC:tegra194 = ""

BOOTBINS_MACHINE_SPECIFIC:tegra210 = "\
    cboot_rb.bin \
    nvtboot.bin \
    nvtboot_rb.bin \
    sc7entry-firmware.bin \
    warmboot.bin \
"
do_compile() {
    :
}

do_compile:append:tegra186() {
    ${STAGING_BINDIR_NATIVE}/tegra186-flash/nv_smd_generator ${SMD_CFG} ${B}/slot_metadata.bin
}


do_compile:append:tegra194() {
    ${STAGING_BINDIR_NATIVE}/tegra186-flash/nv_smd_generator ${SMD_CFG} ${B}/slot_metadata.bin
    if [ -n "${CBOOTOPTION_FILE}" ]; then
        dtc -I dts -O dtb -o ${B}/cbo.dtb ${CBOOTOPTION_FILE}
    fi
    for f in ${S}/bootloader/${NVIDIA_BOARD}/tegra194-*-bpmp-*.dtb; do
        compressedfile=${B}/$(basename "$f" .dtb)_lz4.dtb
        lz4c -f $f $compressedfile
    done
}

do_install() {
    PATH="${STAGING_BINDIR_NATIVE}/${FLASHTOOLS_DIR}:$PATH"
    install -d ${D}${datadir}/tegraflash
    install -m 0644 ${S}/nv_tegra/bsp_version ${D}${datadir}/tegraflash/
    for f in ${BOOTBINS}; do
        install -m 0644 ${S}/bootloader/$f ${D}${datadir}/tegraflash
    done
    for f in ${BOOTBINS_MACHINE_SPECIFIC}; do
        install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/$f ${D}${datadir}/tegraflash
    done
    if [ -n "${PARTITION_FILE_EXTERNAL}" -a -n "${PARTITION_LAYOUT_EXTERNAL}" ]; then
        # For flashing to an external (USB/NVMe) device on targets where
        # some of the boot partitions spill into the eMMC, preprocess the
        # the XML files so the layout for the internal storage retains the
        # those boot partitions.  For TX2 platforms, the kernel/DTBs/etc.
        # partitions must be moved and filtered out of the external layout.
        # That filtering is not required on Xavier.
        if [ "${TNSPEC_BOOTDEV}" != "mmcblk0p1" -a "${BOOT_PARTITIONS_ON_EMMC}" = "1" ]; then
            nvflashxmlparse --split=/dev/null --output=${D}${datadir}/tegraflash/${PARTITION_LAYOUT_TEMPLATE} ${EXTRA_XML_SPLIT_ARGS} ${PARTITION_FILE}
	    chmod 0644 ${D}${datadir}/tegraflash/${PARTITION_LAYOUT_TEMPLATE}
	    if ${FILTER_EXTERNAL_PARTITIONS}; then
                nvflashxmlparse --remove --output=${D}${datadir}/tegraflash/${PARTITION_LAYOUT_EXTERNAL} ${PARTITION_FILE_EXTERNAL}
		chmod 0644 ${D}${datadir}/tegraflash/${PARTITION_LAYOUT_EXTERNAL}
	    else
		# BUP generation will fail if the main XML file does not
		# contain the kernel/DTB/etc, so save a copy of the
		# original for that purpose.
		install -m 0644 ${PARTITION_FILE} ${D}${datadir}/tegraflash/bupgen-${PARTITION_LAYOUT_TEMPLATE}
		install -m 0644 ${PARTITION_FILE_EXTERNAL} ${D}${datadir}/tegraflash/${PARTITION_LAYOUT_EXTERNAL}
	    fi
        else
	    install -m 0644 ${PARTITION_FILE} ${D}${datadir}/tegraflash/${PARTITION_LAYOUT_TEMPLATE}
	    install -m 0644 ${PARTITION_FILE_EXTERNAL} ${D}${datadir}/tegraflash/${PARTITION_LAYOUT_EXTERNAL}
        fi
    elif [ -n "${PARTITION_FILE_EXTERNAL_FALLBACK}" -a -n "${PARTITION_LAYOUT_EXTERNAL}" ]; then
        # Synthesize an external layout from the fallback (SDcard)
        # layout (for t210 platforms)
        nvflashxmlparse --split=${D}${datadir}/tegraflash/${PARTITION_LAYOUT_EXTERNAL} --output=/dev/null --change-device-type=nvme ${PARTITION_FILE_EXTERNAL_FALLBACK}
	chmod 0644 ${D}${datadir}/tegraflash/${PARTITION_LAYOUT_EXTERNAL}
	# eMMC-based platforms store boot partitions on eMMC, make
	# sure the eMMC layout is retained for those
        if [ "${TNSPEC_BOOTDEV}" != "mmcblk0p1" -a "${BOOT_PARTITIONS_ON_EMMC}" = "1" ]; then
            nvflashxmlparse --split=/dev/null --output=${D}${datadir}/tegraflash/${PARTITION_LAYOUT_TEMPLATE} ${EXTRA_XML_SPLIT_ARGS} ${PARTITION_FILE}
	    chmod 0644 ${D}${datadir}/tegraflash/${PARTITION_LAYOUT_TEMPLATE}
	else
	    install -m 0644 ${PARTITION_FILE} ${D}${datadir}/tegraflash/${PARTITION_LAYOUT_TEMPLATE}
	fi
    else
	install -m 0644 ${PARTITION_FILE} ${D}${datadir}/tegraflash/${PARTITION_LAYOUT_TEMPLATE}
    fi
    [ -z "${ODMFUSE_FILE}" ] || install -m 0644 ${ODMFUSE_FILE} ${D}${datadir}/tegraflash/odmfuse_pkc_${MACHINE}.xml
}

do_install:append:tegra186() {
    install -m 0644 ${BCT_TEMPLATE} ${D}${datadir}/tegraflash/${EMMC_BCT}
    install -m 0644 ${B}/slot_metadata.bin ${D}${datadir}/tegraflash/
    install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/BCT/tegra186* ${D}${datadir}/tegraflash/
    install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/tegra186*bpmp*dtb ${D}${datadir}/tegraflash/
    install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/BCT/minimal_scr.cfg ${D}${datadir}/tegraflash/
    install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/BCT/mobile_scr.cfg ${D}${datadir}/tegraflash/
    install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/BCT/emmc.cfg ${D}${datadir}/tegraflash/
}

do_install:append:jetson-xavier-nx-devkit-tx2-nx() {
    # XXX only 16GiB eMMC on tx2-nx
    sed -i -e's,num_sectors="61071360",num_sectors="30777344",' ${D}${datadir}/tegraflash/${PARTITION_LAYOUT_TEMPLATE}
}

do_install:append:tegra194() {
    install -m 0644 ${B}/slot_metadata.bin ${D}${datadir}/tegraflash/
    install -m 0644 ${BCT_TEMPLATE} ${D}${datadir}/tegraflash/${EMMC_BCT}
    install -m 0644 ${BCT_OVERRIDE_TEMPLATE} ${D}${datadir}/tegraflash/${EMMC_BCT_OVERRIDE}
    install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/BCT/tegra19* ${D}${datadir}/tegraflash/
    for f in ${S}/bootloader/${NVIDIA_BOARD}/tegra194-*-bpmp-*.dtb; do
        compressedfile=${B}/$(basename "$f" .dtb)_lz4.dtb
	install -m 0644 $compressedfile ${D}${datadir}/tegraflash/$(basename "$f")
    done
    install -m 0644 ${S}/bootloader/xusb_sil_rel_fw ${D}${datadir}/tegraflash/
    if [ -n "${CBOOTOPTION_FILE}" ]; then
        install -m 0644 ${B}/cbo.dtb ${D}${datadir}/tegraflash/
    fi
}

do_install:append:tegra210() {
    install -m 0644 ${BCT_TEMPLATE} ${D}${datadir}/tegraflash/${MACHINE}.cfg
    [ -z "${NVIDIA_BOARD_CFG}" ] || install -m 0644 ${BOARD_CFG} ${D}${datadir}/tegraflash/board_config_${MACHINE}.xml
}

PACKAGES = "${PN}-dev"
FILES:${PN}-dev = "${datadir}"
RDEPENDS:${PN}-dev = ""
PACKAGE_ARCH = "${MACHINE_ARCH}"
