DESCRIPTION = "Storage layout XML definitions"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

COMPATIBLE_MACHINE = "(tegra)"
INHIBIT_DEFAULT_DEPS = "1"

DEPENDS = "tegra-helper-scripts-native tegra-storage-layout-base"

PARTITION_FILE ?= "${STAGING_DATADIR}/l4t-storage-layout/${PARTITION_LAYOUT_TEMPLATE}"
PARTITION_FILE_EXTERNAL ?= "${STAGING_DATADIR}/l4t-storage-layout/${PARTITION_LAYOUT_EXTERNAL}"
EXTRA_XML_SPLIT_ARGS = "--change-device-type=sdcard"
PATH =. "${STAGING_BINDIR_NATIVE}/tegra-flash:"

# Applies any fixed, per-SoC rewrites.  Consult the flash.sh script
# and conf files in the L4T kit for how these are derived.
copy_in_flash_layout() {
    local srcfile="$1"
    local dstfile="$2"
    cp "$srcfile" "$dstfile"
    case "${SOC_FAMILY}" in
        tegra194)
            # Multiple seds here since the 2nd occurrence of MB1FILE
            # gets a different rewrite than the first
            sed -i -e':a;N;$!ba;s,MB1FILE,mb1_b_t194_prod.bin,2' "$dstfile"
            sed -i -e"s,TEGRABOOT,nvtboot_t194.bin," \
                    -e"s,MTSPREBOOT,preboot_c10_prod_cr.bin," \
                    -e"s,MTS_MCE,mce_c10_prod_cr.bin," \
                    -e"s,MTSPROPER,mts_c10_prod_cr.bin," \
                    -e"s,SCEFILE,sce_t194.bin," \
                    -e"s,MB1FILE,mb1_t194_prod.bin," \
                    -e"s,BPFFILE,bpmp-2_t194.bin," \
                    -e"s,TBCFILE,uefi_jetson.bin," \
                    -e"s,CAMERAFW,camera-rtcpu-t194-rce.img," \
                    -e"s,DRAMECCTYPE,dram_ecc," -e"s,DRAMECCFILE,dram-ecc-t194.bin," -e"s,DRAMECCNAME,dram-ecc-fw," \
                    -e"s,BADPAGETYPE,black_list_info," -e"s,BADPAGEFILE,badpage.bin," -e"s,BADPAGENAME,badpage-fw," \
                    -e"s,SPEFILE,spe_t194.bin," \
                    -e"s,WB0BOOT,warmboot_t194_prod.bin," \
                    "$dstfile"
            ;;
        tegra234)
            sed -i -e"s,MB1FILE,mb1_t234_prod.bin," \
                -e"s,CAMERAFW,camera-rtcpu-t234-rce.img," \
                -e"s,SPEFILE,spe_t234.bin," \
                -e"s,BADPAGETYPE,black_list_info," -e"s,BADPAGEFILE,badpage.bin," -e"s,BADPAGENAME,bad-page," \
                -e"s,FSIFW,fsi-fw-ecc.bin," \
                -e"s,PSCBL1FILE,psc_bl1_t234_prod.bin," \
                -e"s,TSECFW,," \
                -e"s,NVHOSTNVDEC,nvdec_t234_prod.fw," \
                -e"s,MB2BLFILE,mb2_t234.bin," \
                -e"s,XUSB_FW,xusb_t234_prod.bin," \
                -e"s,PSCFW,pscfw_t234_prod.bin," \
                -e"s,MCE_IMAGE,mce_flash_o10_cr_prod.bin," \
                -e"s,WB0FILE,sc7_t234_prod.bin," \
                -e"s,PSCRF_IMAGE,psc_rf_t234_prod.bin," \
                -e"s,MB2RF_IMAGE,mb2rf_t234.bin," \
                -e"s,TBCDTB-FILE,uefi_jetson_with_dtb.bin," \
                -e"s,DCE,display-t234-dce.bin," \
                "$dstfile"
            ;;
        *)
            bberror "Unrecognized SOC_FAMILY: ${SOC_FAMILY}"
            ;;
    esac
}

do_compile() {
    copy_in_flash_layout ${PARTITION_FILE} internal-flash.xml.orig
    # For flashing to an external (USB/NVMe) device on targets where
    # some of the boot partitions spill into the eMMC, preprocess the
    # the XML files so the layout for the internal storage retains the
    # those boot partitions, and remove the names of those partitions
    # from the layout for the external storage.
    if [ "${TNSPEC_BOOTDEV}" != "mmcblk0p1" -a "${BOOT_PARTITIONS_ON_EMMC}" = "1" ]; then
        nvflashxmlparse -v --split=/dev/null --output=internal-flash.xml ${EXTRA_XML_SPLIT_ARGS} internal-flash.xml.orig
        # BUP generation will fail if the main XML file does not
        # contain the kernel/DTB/etc, so save a copy of the
        # original for that purpose, stripping out the APP
        # partition so that the offsets of the partitions
        # referenced during early boot match the split layout above.
        nvflashxmlparse -v --remove --partitions-to-remove=APP,APP_b --output=bupgen-internal-flash.xml internal-flash.xml.orig
    else
        if [ "${TEGRAFLASH_NO_INTERNAL_STORAGE}" = "1" ]; then
            # For modules with *only* SPI flash (or other boot device) and no
            # internal storage for rootfs, use a full copy for BUP (see note above)
            # and make sure the internal layout used for flashing only
            # covers the boot device.
            cp internal-flash.xml.orig bupgen-internal-flash.xml
            nvflashxmlparse --extract -t boot --output=internal-flash.xml internal-flash.xml.orig
        else
            cp internal-flash.xml.orig internal-flash.xml
        fi
    fi

    if [ -n "${PARTITION_LAYOUT_EXTERNAL}" ]; then
        copy_in_flash_layout ${PARTITION_FILE_EXTERNAL} external-flash.xml
    fi

}

do_install() {
    install -D -m 0644 -t ${D}${datadir}/tegraflash ${B}/internal-flash.xml
    if [ -e ${B}/bupgen-internal-flash.xml ]; then
         install -m 0644 ${B}/bupgen-internal-flash.xml ${D}${datadir}/tegraflash/bupgen-internal-flash.xml
    else
        ln -sf internal-flash.xml ${D}${datadir}/tegraflash/bupgen-internal-flash.xml
    fi
    if [ -e ${B}/external-flash.xml ]; then
        install -m 0644 ${B}/external-flash.xml ${D}${datadir}/tegraflash/
    fi
}

PACKAGES = "${PN}-dev"
FILES:${PN}-dev = "${datadir}"
RRECOMMENDS:${PN}-dev = ""
PACKAGE_ARCH = "${MACHINE_ARCH}"
