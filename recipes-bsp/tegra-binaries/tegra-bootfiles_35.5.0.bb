require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

COMPATIBLE_MACHINE = "(tegra)"
INHIBIT_DEFAULT_DEPS = "1"
DEPENDS = "tegra-flashtools-native dtc-native tegra-flashvars lz4-native coreutils-native"

BCT_TEMPLATE ?= "${S}/bootloader/${NVIDIA_BOARD}/BCT/${EMMC_BCT}"
BCT_OVERRIDE_TEMPLATE ?= "${S}/bootloader/${NVIDIA_BOARD}/BCT/${EMMC_BCT_OVERRIDE}"
BOARD_CFG ?= "${S}/bootloader/${NVIDIA_BOARD}/cfg/${NVIDIA_BOARD_CFG}"
PARTITION_FILE ?= "${S}/bootloader/${NVIDIA_BOARD}/cfg/${PARTITION_LAYOUT_TEMPLATE}"
PARTITION_FILE_EXTERNAL ?= "${S}/tools/kernel_flash/${PARTITION_LAYOUT_EXTERNAL}"
EXTRA_XML_SPLIT_ARGS = "--change-device-type=sdcard"
ODMFUSE_FILE ?= ""

BOOTBINS:tegra194 = "\
    adsp-fw.bin \
    bpmp-2_t194.bin \
    camera-rtcpu-t194-rce.img \
    dram-ecc-t194.bin \
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
    xusb_sil_rel_fw \
    warmboot_t194_prod.bin \
    sce_t194.bin \
    dram-ecc-t194.bin \
    badpage.bin \
"

BOOTBINS:tegra234 = "\
    adsp-fw.bin \
    applet_t234.bin \
    ${BPF_FILE} \
    camera-rtcpu-t234-rce.img \
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
    readinfo_t234_min_prod.xml \
    camera-rtcpu-sce.img \
    fsi-fw-ecc.bin \
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

do_compile:append:tegra194() {
    BL_DIR="${S}/bootloader"
    # Create badpage.bin if it doesn't exist
    if [ ! -f "${BL_DIR}/badpage.bin" ]; then
        echo "creating dummy ${BL_DIR}/badpage.bin"
        dd if=/dev/zero of="${BL_DIR}/badpage.bin" bs=4096 count=1;
    else
        echo "reusing existing ${BL_DIR}/badpage.bin"
        # Clear BCH Header
        dd if=/dev/zero of="${BL_DIR}/badpage.bin" bs=4096 seek=0 count=1;
    fi;
    printf 'NVDA' | dd of="${BL_DIR}/badpage.bin" bs=1 seek=0 count=4 conv=notrunc &> /dev/null

    # Originally, the statement below used "printf '\x01'" to write 0x01 to the shell. But because of bitbake, \x01 won't be interpreted correctly
    # Therefore, python is used to write 0x01 to the shell
    python3 -c 'import sys; sys.stdout.buffer.write(bytes([0x01]))' | dd of="${BL_DIR}/badpage.bin" bs=1 seek=2976 count=1 conv=notrunc &> /dev/null
    printf 'BINF' | dd of="${BL_DIR}/badpage.bin" bs=1 seek=2992 count=4 conv=notrunc &> /dev/null
}

do_compile:append:tegra234() {
    BL_DIR="${S}/bootloader"
    # Create badpage.bin if it doesn't exist
    if [ ! -f "${BL_DIR}/badpage.bin" ]; then
        echo "creating dummy ${BL_DIR}/badpage.bin"
        dd if=/dev/zero of="${BL_DIR}/badpage.bin" bs=4096 count=1;
    else
        echo "reusing existing ${BL_DIR}/badpage.bin"
        # Clear BCH Header
        dd if=/dev/zero of="${BL_DIR}/badpage.bin" bs=4096 seek=0 count=1;
    fi;
    printf 'NVDA' | dd of="${BL_DIR}/badpage.bin" bs=1 seek=0 count=4 conv=notrunc &> /dev/null
    printf 'BINF' | dd of="${BL_DIR}/badpage.bin" bs=1 seek=5120 count=4 conv=notrunc &> /dev/null
}

install_external_layout() {
    bberror "No method for installing external partition layout file"
}

install_external_layout:tegra194() {
    install -m 0644 ${PARTITION_FILE_EXTERNAL} ${D}${datadir}/tegraflash/${PARTITION_LAYOUT_EXTERNAL}
}

install_external_layout:tegra234() {
    nvflashxmlparse -v --switch-to-prefixed-kernel-partitions --output=${D}${datadir}/tegraflash/${PARTITION_LAYOUT_EXTERNAL} ${PARTITION_FILE_EXTERNAL}
    chmod 0644 ${D}${datadir}/tegraflash/${PARTITION_LAYOUT_EXTERNAL}
}

do_install() {
    PATH="${STAGING_BINDIR_NATIVE}/tegra-flash:${PATH}"
    install -d ${D}${datadir}/tegraflash
    install -m 0644 ${S}/nv_tegra/bsp_version ${D}${datadir}/tegraflash/
    for f in ${BOOTBINS}; do
        install -m 0644 ${S}/bootloader/$f ${D}${datadir}/tegraflash
    done
    for f in ${BOOTBINS_MACHINE_SPECIFIC}; do
        install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/$f ${D}${datadir}/tegraflash
    done

    # For flashing to an external (USB/NVMe) device on targets where
    # some of the boot partitions spill into the eMMC, preprocess the
    # the XML files so the layout for the internal storage retains the
    # those boot partitions, and remove the names of those partitions
    # from the layout for the external storage.
    if [ "${TNSPEC_BOOTDEV}" != "mmcblk0p1" -a "${BOOT_PARTITIONS_ON_EMMC}" = "1" ]; then
        nvflashxmlparse -v --split=/dev/null --output=${D}${datadir}/tegraflash/${PARTITION_LAYOUT_TEMPLATE} ${EXTRA_XML_SPLIT_ARGS} ${PARTITION_FILE}
	chmod 0644 ${D}${datadir}/tegraflash/${PARTITION_LAYOUT_TEMPLATE}
	# BUP generation will fail if the main XML file does not
	# contain the kernel/DTB/etc, so save a copy of the
	# original for that purpose, stripping out the APP
	# partition so that the offsets of the partitions
	# referenced during early boot match the split layout above.
	nvflashxmlparse -v --remove --partitions-to-remove=APP,APP_b --output=${D}${datadir}/tegraflash/bupgen-${PARTITION_LAYOUT_TEMPLATE} ${PARTITION_FILE}
	chmod 0644 ${D}${datadir}/tegraflash/bupgen-${PARTITION_LAYOUT_TEMPLATE}
	install -m 0644 ${PARTITION_FILE_EXTERNAL} ${D}${datadir}/tegraflash/${PARTITION_LAYOUT_EXTERNAL}
    else
	if [ "${TEGRAFLASH_NO_INTERNAL_STORAGE}" = "1" ]; then
	    install -m 0644 ${PARTITION_FILE} ${D}${datadir}/tegraflash/bupgen-${PARTITION_LAYOUT_TEMPLATE}
            nvflashxmlparse --extract -t boot --output=${D}${datadir}/tegraflash/${PARTITION_LAYOUT_TEMPLATE} ${PARTITION_FILE}
	    chmod 0644 ${D}${datadir}/tegraflash/${PARTITION_LAYOUT_TEMPLATE}
	else
	    install -m 0644 ${PARTITION_FILE} ${D}${datadir}/tegraflash/${PARTITION_LAYOUT_TEMPLATE}
	fi
	install_external_layout
    fi
    [ -z "${ODMFUSE_FILE}" ] || install -m 0644 ${ODMFUSE_FILE} ${D}${datadir}/tegraflash/odmfuse_pkc_${MACHINE}.xml
    install -m 0644 ${BCT_TEMPLATE} ${D}${datadir}/tegraflash/${EMMC_BCT}
}

do_install:append:tegra194() {
    install -m 0644 ${S}/bootloader/eks_t194.img ${D}${datadir}/tegraflash/eks.img
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
    install -m 0644 ${S}/bootloader/eks_t234.img ${D}${datadir}/tegraflash/eks.img
    install -m 0644 ${S}/bootloader/tegra234-*.dts* ${D}${datadir}/tegraflash/
    install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/tegra234-bpmp-*.dtb ${D}${datadir}/tegraflash/
    install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/BCT/tegra234* ${D}${datadir}/tegraflash/
    install -m 0644 ${S}/bootloader/bpmp_t234-*.bin ${D}${datadir}/tegraflash/
    install -m 0644 ${S}/bootloader/badpage.bin ${D}${datadir}/tegraflash/
}

PACKAGES = "${PN}-dev"
FILES:${PN}-dev = "${datadir}"
RDEPENDS:${PN}-dev = ""
PACKAGE_ARCH = "${MACHINE_ARCH}"
