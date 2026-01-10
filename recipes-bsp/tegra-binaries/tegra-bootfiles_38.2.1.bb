require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

COMPATIBLE_MACHINE = "(tegra)"
INHIBIT_DEFAULT_DEPS = "1"
DEPENDS = "tegra-flashvars tegra-storage-layout tegra-eks-image dtc-native coreutils-native lz4-native"

BCT_TEMPLATE ?= "${S}/bootloader/${NVIDIA_BOARD}/BCT/${EMC_BCT}"
BCT_OVERRIDE_TEMPLATE ?= "${S}/bootloader/${NVIDIA_BOARD}/BCT/${EMC_BCT_OVERRIDE}"
TEGRA_SOCNAME_SHORT = "${@d.getVar('SOC_FAMILY')[0:1] + d.getVar('SOC_FAMILY')[-3:]}"
# Work around bitbake parsing quirk with shell-style escapes
BACKSLASH_X_01 = "${@'\\' + 'x01'}"
BADPAGE_SIZE = "8192"

TEGRA_MB1_LOG_LEVEL ??= "4"
TEGRA_MB1_MISC_CONFIG_SECTION_DEFAULT = "misc"
TEGRA_MB1_MISC_CONFIG_SECTION_DEFAULT:tegra264 = "mb1_bct"
TEGRA_MB1_MISC_CONFIG_SECTION ?= "${TEGRA_MB1_MISC_CONFIG_SECTION_DEFAULT}"
TEGRA_BPMP_SERIAL_LOGGING ??= "1"

do_compile() {
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
        tegra234)
	    printf "${BACKSLASH_X_01}" | dd of="badpage.bin" bs=1 seek=4064 count=1 conv=notrunc &> /dev/null
            printf 'BINF' | dd of="badpage.bin" bs=1 seek=5120 count=4 conv=notrunc &> /dev/null
            ;;
        tegra264)
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

    install -m 0644 ${BCT_TEMPLATE} ${D}${datadir}/tegraflash/${EMC_BCT}
    if [ -n "${TEGRA_FLASHVAR_DEV_PARAMS}" -a -e "${D}${datadir}/tegraflash/${TEGRA_FLASHVAR_DEV_PARAMS}" ]; then
        sed -i "s/preprod_dev_sign = <1>/preprod_dev_sign = <0>/" ${D}${datadir}/tegraflash/${TEGRA_FLASHVAR_DEV_PARAMS}
    fi
    if [ -n "${TEGRA_FLASHVAR_DEV_PARAMS_B}" -a -e "${D}${datadir}/tegraflash/${TEGRA_FLASHVAR_DEV_PARAMS_B}" ]; then
        sed -i "s/preprod_dev_sign = <1>/preprod_dev_sign = <0>/" ${D}${datadir}/tegraflash/${TEGRA_FLASHVAR_DEV_PARAMS_B}
    fi
    if [ -n "${TEGRA_FLASHVAR_EMC_FUSE_DEV_PARAMS}" -a -e "${D}${datadir}/tegraflash/${TEGRA_FLASHVAR_EMC_FUSE_DEV_PARAMS}" ]; then
        sed -i "s/preprod_dev_sign = <1>/preprod_dev_sign = <0>/" ${D}${datadir}/tegraflash/${TEGRA_FLASHVAR_EMC_FUSE_DEV_PARAMS}
    fi
}

install_other_boot_firmware_files() {
    case "${SOC_FAMILY}" in
	tegra234)
	    install -m 0644 ${S}/bootloader/tegra234-*.dts* ${D}${datadir}/tegraflash/
	    install -m 0644 ${S}/bootloader/fuse_t234.xml ${D}${datadir}/tegraflash/
	    install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/tegra234-bpmp-*.dtb ${D}${datadir}/tegraflash/
	    install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/BCT/tegra234* ${D}${datadir}/tegraflash/
	    install -m 0644 ${S}/bootloader/bpmp_t234-*.bin ${D}${datadir}/tegraflash/
	    ;;
	tegra264)
	    install -m 0644 ${S}/bootloader/tegra264-*.dts* ${D}${datadir}/tegraflash/
	    install -m 0644 ${S}/bootloader/fuse_t264.xml ${D}${datadir}/tegraflash/
	    install -m 0644 ${S}/bootloader/tegra264-p3834-0008-* ${D}${datadir}/tegraflash/
	    install -m 0644 ${S}/bootloader/tegra264-mb1-bct-* ${D}${datadir}/tegraflash/
	    install -m 0644 ${S}/bootloader/t264_tegrabl_* ${D}${datadir}/tegraflash/
	    install -m 0644 ${S}/bootloader/t264-pinctrl-tegra.h ${D}${datadir}/tegraflash/
	    install -m 0644 ${S}/bootloader/tegra264-gpio.h ${D}${datadir}/tegraflash/
	    install -m 0644 ${S}/bootloader/tegra264-firewall-config-base.dtsi ${D}${datadir}/tegraflash/
	    install -m 0644 ${S}/bootloader/tegra264-mb2-bct-common.dtsi ${D}${datadir}/tegraflash/
	    install -m 0644 ${S}/bootloader/bpmp_t264-*.bin ${D}${datadir}/tegraflash/
	    install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/tegra264-bpmp-*.dtb ${D}${datadir}/tegraflash/
	    install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/BCT/tegra264* ${D}${datadir}/tegraflash/
	    install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/bctflags/platform_config_profile.yaml ${D}${datadir}/tegraflash/
	    ;;
	*)
	    bberror "Unrecognized SOC_FAMILY: ${SOC_FAMILY}"
	    ;;
    esac
    install -m 0644 ${B}/badpage.bin ${D}${datadir}/tegraflash/
    cat >> ${D}${datadir}/tegraflash/${TEGRA_FLASHVAR_MISC_CONFIG} <<EOF
/ {
        ${TEGRA_MB1_MISC_CONFIG_SECTION} {
                debug {
                        log_level = <${TEGRA_MB1_LOG_LEVEL}>;
                };
        };
};
EOF
    if ${@'false' if bb.utils.to_boolean(d.getVar('TEGRA_BPMP_SERIAL_LOGGING')) else 'true'}; then
        fdtput -r ${D}${datadir}/tegraflash/${TEGRA_FLASHVAR_BPFDTB_FILE} /serial
    fi
}

PACKAGES = "${PN}-dev"
FILES:${PN}-dev = "${datadir}"
RRECOMMENDS:${PN}-dev = ""
PACKAGE_ARCH = "${MACHINE_ARCH}"
