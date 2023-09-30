require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

COMPATIBLE_MACHINE = "(tegra)"
INHIBIT_DEFAULT_DEPS = "1"
DEPENDS = "tegra-flashtools-native dtc-native tegra-flashvars lz4-native coreutils-native"

BCT_TEMPLATE ?= "${S}/bootloader/${NVIDIA_BOARD}/BCT/${EMMC_BCT}"
BCT_OVERRIDE_TEMPLATE ?= "${S}/bootloader/${NVIDIA_BOARD}/BCT/${EMMC_BCT_OVERRIDE}"
PARTITION_FILE ?= "${S}/bootloader/${NVIDIA_BOARD}/cfg/${PARTITION_LAYOUT_TEMPLATE}"
PARTITION_FILE_EXTERNAL ?= "${S}/tools/kernel_flash/${PARTITION_LAYOUT_EXTERNAL}"
EXTRA_XML_SPLIT_ARGS = "--change-device-type=sdcard"
ODMFUSE_FILE ?= ""
PATH =. "${STAGING_BINDIR_NATIVE}/tegra-flash:"
TEGRA_SOCNAME_SHORT = "${@d.getVar('SOC_FAMILY')[0:1] + d.getVar('SOC_FAMILY')[-3:]}"
# Work around bitbake parsing quirk with shell-style escapes
BACKSLASH_X_01 = "${@'\\' + 'x01'}"
BADPAGE_SIZE = "8192"
BADPAGE_SIZE:tegra194 = "4096"

do_compile() {
    if [ "${SOC_FAMILY}" = "tegra194" ]; then
        for f in ${S}/bootloader/${NVIDIA_BOARD}/tegra194-*-bpmp-*.dtb; do
            compressedfile=${B}/$(basename "$f" .dtb)_lz4.dtb
            lz4c -f $f $compressedfile
	done
	cp ${S}/bootloader/nvdisp-init.bin ${B}
	truncate --size=393216 ${B}/nvdisp-init.bin
    fi
    prepare_badpage_mapfile
    prepare_external_flash_layout
}

prepare_badpage_mapfile()  {
    if [ ! -f "${S}/bootloader/badpage.bin" ]; then
        echo "creating dummy badpage.bin"
        dd if=/dev/zero of="badpage.bin" bs=${BADPAGE_SIZE} count=1;
    else
        echo "reusing existing ${S}/bootloader/badpage.bin"
	cp ${S}/bootloader/badpage.bin ${B}
        # Clear BCH Header
        dd if=/dev/zero of="badpage.bin" bs=${BADPAGE_SIZE} seek=0 count=1;
    fi
    printf 'NVDA' | dd of="badpage.bin" bs=1 seek=0 count=4 conv=notrunc &> /dev/null

    case "${SOC_FAMILY}" in
        tegra194)
	    printf "${BACKSLASH_X_01}" | dd of="badpage.bin" bs=1 seek=2976 count=1 conv=notrunc &> /dev/null
	    printf 'BINF' | dd of="badpage.bin" bs=1 seek=2992 count=4 conv=notrunc &> /dev/null
            ;;
        tegra234)
            printf 'BINF' | dd of="badpage.bin" bs=1 seek=5120 count=4 conv=notrunc &> /dev/null
            ;;
        *)
            bberror "Unrecognized SOC_FAMILY: ${SOC_FAMILY}"
            ;;
    esac
}

prepare_external_flash_layout() {

    [ -n "${PARTITION_LAYOUT_EXTERNAL}" ] || return

    case "${SOC_FAMILY}" in
	tegra194)
	    cp ${PARTITION_FILE_EXTERNAL} ${PARTITION_LAYOUT_EXTERNAL}
	    ;;
	tegra234)
	    nvflashxmlparse -v --switch-to-prefixed-kernel-partitions --output=${PARTITION_LAYOUT_EXTERNAL} ${PARTITION_FILE_EXTERNAL}
            ;;
	*)
	    bberror "Unrecognized SOC_FAMILY: ${SOC_FAMILY}"
	    ;;
    esac
}
install_external_layout:tegra194() {
}

install_external_layout:tegra234() {
    nvflashxmlparse -v --switch-to-prefixed-kernel-partitions --output=${D}${datadir}/tegraflash/${PARTITION_LAYOUT_EXTERNAL} ${PARTITION_FILE_EXTERNAL}
    chmod 0644 ${D}${datadir}/tegraflash/${PARTITION_LAYOUT_EXTERNAL}
}

do_install() {
    install -d ${D}${datadir}/tegraflash
    install -m 0644 ${S}/nv_tegra/bsp_version ${D}${datadir}/tegraflash/
    for f in ${TEGRA_BOOT_FIRMWARE_FILES}; do
        install -m 0644 ${S}/bootloader/$f ${D}${datadir}/tegraflash
    done
    install_other_boot_firmware_files

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
	nvflashxmlparse -v --remove --partitions-to-remove=APP --output=${D}${datadir}/tegraflash/bupgen-${PARTITION_LAYOUT_TEMPLATE} ${PARTITION_FILE}
	chmod 0644 ${D}${datadir}/tegraflash/bupgen-${PARTITION_LAYOUT_TEMPLATE}
    else
	if [ "${TEGRAFLASH_NO_INTERNAL_STORAGE}" = "1" ]; then
	    install -m 0644 ${PARTITION_FILE} ${D}${datadir}/tegraflash/bupgen-${PARTITION_LAYOUT_TEMPLATE}
            nvflashxmlparse --extract -t boot --output=${D}${datadir}/tegraflash/${PARTITION_LAYOUT_TEMPLATE} ${PARTITION_FILE}
	    chmod 0644 ${D}${datadir}/tegraflash/${PARTITION_LAYOUT_TEMPLATE}
	else
	    install -m 0644 ${PARTITION_FILE} ${D}${datadir}/tegraflash/${PARTITION_LAYOUT_TEMPLATE}
	fi
    fi
    [ -z "${PARTITION_LAYOUT_EXTERNAL}" ] || install -m 0644 ${B}/${PARTITION_LAYOUT_EXTERNAL} ${D}${datadir}/tegraflash/
    [ -z "${ODMFUSE_FILE}" ] || install -m 0644 ${ODMFUSE_FILE} ${D}${datadir}/tegraflash/odmfuse_pkc_${MACHINE}.xml
    install -m 0644 ${BCT_TEMPLATE} ${D}${datadir}/tegraflash/${EMMC_BCT}
}

install_other_boot_firmware_files() {
    install -m 0644 ${S}/bootloader/eks_${TEGRA_SOCNAME_SHORT}.img ${D}${datadir}/tegraflash/eks.img
    case "${SOC_FAMILY}" in
	tegra194)
	    install -m 0644 ${B}/nvdisp-init.bin ${D}${datadir}/tegraflash/
	    install -m 0644 ${BCT_OVERRIDE_TEMPLATE} ${D}${datadir}/tegraflash/${EMMC_BCT_OVERRIDE}
	    install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/BCT/tegra19* ${D}${datadir}/tegraflash/
	    for f in ${S}/bootloader/${NVIDIA_BOARD}/tegra194-*-bpmp-*.dtb; do
		install -m 0644 $f ${D}${datadir}/tegraflash/
		compressedfile=${B}/$(basename "$f" .dtb)_lz4.dtb
		install -m 0644 $compressedfile ${D}${datadir}/tegraflash/
	    done
	    ;;
	tegra234)
	    install -m 0644 ${S}/bootloader/tegra234-*.dts* ${D}${datadir}/tegraflash/
	    install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/tegra234-bpmp-*.dtb ${D}${datadir}/tegraflash/
	    install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/BCT/tegra234* ${D}${datadir}/tegraflash/
	    install -m 0644 ${S}/bootloader/bpmp_t234-*.bin ${D}${datadir}/tegraflash/
	    ;;
	*)
	    bberror "Unrecognized SOC_FAMILY: ${SOC_FAMILY}"
	    ;;
    esac
    install -m 0644 ${B}/badpage.bin ${D}${datadir}/tegraflash/
}

PACKAGES = "${PN}-dev"
FILES:${PN}-dev = "${datadir}"
RRECOMMENDS:${PN}-dev = ""
PACKAGE_ARCH = "${MACHINE_ARCH}"
