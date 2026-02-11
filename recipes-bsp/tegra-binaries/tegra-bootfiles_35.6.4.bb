require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

COMPATIBLE_MACHINE = "(tegra)"
INHIBIT_DEFAULT_DEPS = "1"
DEPENDS = "tegra-flashvars tegra-storage-layout tegra-eks-image dtc-native coreutils-native lz4-native"

BCT_TEMPLATE ?= "${S}/bootloader/${NVIDIA_BOARD}/BCT/${EMMC_BCT}"
BCT_OVERRIDE_TEMPLATE ?= "${S}/bootloader/${NVIDIA_BOARD}/BCT/${EMMC_BCT_OVERRIDE}"
ODMFUSE_FILE ?= ""
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

do_install() {
    install -d ${D}${datadir}/tegraflash
    install -m 0644 ${S}/nv_tegra/bsp_version ${D}${datadir}/tegraflash/
    for f in ${TEGRA_BOOT_FIRMWARE_FILES}; do
        install -m 0644 ${S}/bootloader/$f ${D}${datadir}/tegraflash
    done
    install_other_boot_firmware_files

    [ -z "${ODMFUSE_FILE}" ] || install -m 0644 ${ODMFUSE_FILE} ${D}${datadir}/tegraflash/odmfuse_pkc_${MACHINE}.xml
    install -m 0644 ${BCT_TEMPLATE} ${D}${datadir}/tegraflash/${EMMC_BCT}
}

install_other_boot_firmware_files() {
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
