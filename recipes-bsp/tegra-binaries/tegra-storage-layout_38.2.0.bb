DESCRIPTION = "Storage layout XML definitions"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

COMPATIBLE_MACHINE = "(tegra)"
INHIBIT_DEFAULT_DEPS = "1"

DEPENDS = "tegra-helper-scripts-native tegra-storage-layout-base"

PARTITION_FILE ?= "${STAGING_DATADIR}/l4t-storage-layout/${PARTITION_LAYOUT_TEMPLATE}"
PARTITION_FILE_EXTERNAL ?= "${STAGING_DATADIR}/l4t-storage-layout/${PARTITION_LAYOUT_EXTERNAL}"
PARTITION_FILE_RCMBOOT ?= "${STAGING_DATADIR}/l4t-storage-layout/${PARTITION_LAYOUT_RCMBOOT}"
EXTRA_XML_SPLIT_ARGS = "--change-device-type=sdcard"
PATH =. "${STAGING_BINDIR_NATIVE}/tegra-flash:"

S = "${UNPACKDIR}"

# Applies any fixed, per-SoC rewrites.  Consult the flash.sh script
# and conf files in the L4T kit for how these are derived.
copy_in_flash_layout() {
    local srcfile="$1"
    local dstfile="$2"
    cp "$srcfile" "$dstfile"
    case "${SOC_FAMILY}" in
        tegra234)
            sed -i -e"s,MB1FILE,mb1_t234_prod.bin," \
                -e"s,CAMERAFW,camera-rtcpu-t234-rce.img," \
                -e"s,SPEFILE,spe_t234.bin," \
                -e"s,BADPAGETYPE,black_list_info," -e"s,BADPAGEFILE,badpage.bin," -e"s,BADPAGENAME,bad-page," \
                -e"s,FSIFW,fsi-lk.bin," \
                -e"s,PSCBL1FILE,psc_bl1_t234_prod.bin," \
                -e"s,TSECFW,tsec_t234.bin," \
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
                -e"s,PVA_FILE,nvpva_020.fw," \
                "$dstfile"
            ;;
        tegra264)
            sed -i -e"s,MB1FILE,mb1_t264_prod.bin," \
                -e"s,CAMERAFW,camera-rtcpu-t264-rce.img," \
                -e"s,BADPAGETYPE,black_list_info," -e"s,BADPAGEFILE,badpage.bin," -e"s,BADPAGENAME,bad-page," \
                -e"s,PSCBL1FILE,psc_bl1_t264_prod.bin," \
                -e"s,TSECFW,tsec_t264_prod.bin," \
                -e"s,MB2BLFILE,mb2_t264.bin," \
                -e"s,XUSB_FW,xusb_t264_prod.bin," \
                -e"s,PSCFW,pscfw_t264_prod.bin," \
                -e"s,MCE_IMAGE,mce_flash_o10_cr_prod.bin," \
                -e"s,WB0FILE,sc7_t264_prod.bin," \
                -e"s,PSCRF_IMAGE,psc_rf_t264_prod.bin," \
                -e"s,MB2RF_IMAGE,mb2rf_t264.bin," \
                -e"s,TBCDTB-FILE,uefi_t26x_general.bin," \
                -e"s,DCE,display-t264-dce.bin," \
                -e"s,PVA_FILE,nvpva_030.fw," \
                -e"s,RCE1FW,nv-rce1-t264.bin," \
                -e"s,AON_ADSPFW,aon-fw_t264.bin," \
                -e"s,ADSP0FW,adsp0-fw_t264.bin," \
                -e"s,ADSP1FW,adsp1-fw_t264.bin," \
                -e"s,IGBFW,igbfw_gb10b_gsc_package_prod.bin," \
                -e"s,ATF_FW,bl31_t264.fip," \
                -e"s,HAFNIUM_FW,hafnium_t264.fip," \
                -e"s,WB0BOOT,sc7_t264_prod.bin," \
                -e"s,SOSFILE,applet_t264.bin," \
		-e"/FSIFW/d" \
                "$dstfile"
            ;;
        *)
            bberror "Unrecognized SOC_FAMILY: ${SOC_FAMILY}"
            ;;
    esac
}

do_compile() {
    copy_in_flash_layout ${PARTITION_FILE} internal-flash.xml.orig
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

    if [ -n "${PARTITION_LAYOUT_EXTERNAL}" ]; then
        copy_in_flash_layout ${PARTITION_FILE_EXTERNAL} external-flash.xml
    fi
    if [ -n "${PARTITION_LAYOUT_RCMBOOT}" ]; then
        copy_in_flash_layout ${PARTITION_FILE_RCMBOOT} ${PARTITION_LAYOUT_RCMBOOT}
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

    if [ -n "${PARTITION_LAYOUT_RCMBOOT}" ]; then
        install -m 0644 ${B}/${PARTITION_LAYOUT_RCMBOOT} ${D}${datadir}/tegraflash/${PARTITION_LAYOUT_RCMBOOT}
    fi
}

PACKAGES = "${PN}-dev"
FILES:${PN}-dev = "${datadir}"
RRECOMMENDS:${PN}-dev = ""
PACKAGE_ARCH = "${MACHINE_ARCH}"
