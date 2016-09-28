inherit image_types

IMAGE_TYPES += "tegraflash"

DTBFILE ?= "${@os.path.basename(d.getVar('KERNEL_DEVICETREE', True).split()[0])}"

FLASHARGS = ''

# Override this function if you need to add
# customization after the default files are
# copied/symlinked into the working directory
# and before processing begins.
tegraflash_custom_pre() {
    :
}

# Override this function if you need to add
# customization after other processing is done
# but before the zip package is created.
tegraflash_custom_post() {
    :
}

tegraflash_create_flash_config() {
    local destdir="$1"
    local gptsize="$2"
    cat "${STAGING_DATADIR}/tegraflash/flash_${MACHINE}.xml" | sed \
        -e"s,EBTFILE,${IMAGE_UBOOT}-${MACHINE}.${UBOOT_SUFFIX}," \
        -e"/LNXFILE/d" \
        -e"/NCTFILE/d" -e"s,NCTTYPE,data," \
        -e"/SOSFILE/d" \
        -e"s,NXC,NVC," -e"s,NVCTYPE,bootloader," -e"s,NVCFILE,nvtboot.bin," \
        -e"s,MPBTYPE,data," -e"/MPBFILE/d" \
        -e"s,MBPTYPE,data," -e"/MBPFILE/d" \
        -e"s,BXF,BPF," -e"s,BPFFILE,bpmp.bin,"\
        -e"s,WX0,WB0," -e"s,WB0TYPE,WB0," -e"s,WB0FILE,warmboot.bin," \
        -e"s,TXS,TOS," -e"s,TOSFILE,tos.img," \
        -e"/EKSFILE/d" \
        -e"s,FBTYPE,data," -e"/FBFILE/d" \
        -e"s,DXB,DTB," -e"s,DTBFILE,${DTBFILE}," \
        -e"s,APPFILE,${IMAGE_BASENAME}.img," -e"s,APPSIZE,${ROOTFSPART_SIZE}," \
        -e"s,TXC,TBC," -e"s,TBCTYPE,bootloader," -e"s,TBCFILE,nvtboot_cpu.bin," \
	-e"s,EFISIZE,67108864," -e"/EFIFILE/d" \
        -e"s,BCTSIZE,${BOOTPART_SIZE}," -e"s,PPTSIZE,$gptsize," \
        -e"s,PPTFILE,ppt.img," -e"s,GPTFILE,gpt.img," \
        > flash.xml
}

create_tegraflash_pkg() {
    if [ `expr ${BOOTPART_LIMIT} % ${EMMC_DEVSECT_SIZE}` -ne 0 ]; then
       bberror "Boot partition limit must be an even multiple of the device sector size"
       exit 1
    fi
    if [ `expr ${BOOTPART_SIZE} % ${EMMC_DEVSECT_SIZE}` -ne 0 ]; then
       bberror "Boot partition size must be an even multiple of the device sector size"
       exit 1
    fi
    gptsize=`expr ${BOOTPART_LIMIT} - ${BOOTPART_SIZE}`
    if [ $gptsize -lt ${EMMC_DEVSECT_SIZE} ]; then
       bberror "No space for primary GPT"
       exit 1
    fi
    rm -rf "${WORKDIR}/tegraflash"
    mkdir -p "${WORKDIR}/tegraflash"
    oldwd=`pwd`
    cd "${WORKDIR}/tegraflash"
    ln -s "${STAGING_DATADIR}/tegraflash/${MACHINE}.cfg" .
    ln -s "${DEPLOY_DIR_IMAGE}/${IMAGE_UBOOT}-${MACHINE}.${UBOOT_SUFFIX}" .
    ln -s "${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}-${DTBFILE}" ./${DTBFILE}
    ln -s "${STAGING_DATADIR}/tegraflash/board_config_${MACHINE}.xml" .
    ln -s "${STAGING_DATADIR}/tegraflash/cboot.bin" .
    ln -s "${STAGING_DATADIR}/tegraflash/nvtboot_recovery.bin" .
    ln -s "${STAGING_DATADIR}/tegraflash/nvtboot.bin" .
    ln -s "${STAGING_DATADIR}/tegraflash/nvtboot_cpu.bin" .
    ln -s "${STAGING_DATADIR}/tegraflash/warmboot.bin" .
    ln -s "${STAGING_DATADIR}/tegraflash/bpmp.bin" .
    ln -s "${STAGING_DATADIR}/tegraflash/tos.img" .
    tegraflash_custom_pre
    mksparse -v --fillpattern=0 "${DEPLOY_DIR_IMAGE}/${IMAGE_LINK_NAME}.ext3" ${IMAGE_BASENAME}.img
    tegraflash_create_flash_config "${WORKDIR}/tegraflash" $gptsize
    mkgpt -c flash.xml -P ppt.img -t ${EMMC_SIZE} -b ${BOOTPART_SIZE} -s 4KiB -a GPT -v GP1 -V
    rm -f doflash.sh
    cat > doflash.sh <<END
#!/bin/sh
tegraflash.py --bl cboot.bin --bct ${MACHINE}.cfg --odmdata ${ODMDATA} --bldtb ${DTBFILE} --applet nvtboot_recovery.bin \
              --boardconfig board_config_${MACHINE}.xml --cmd "flash;reboot" --cfg flash.xml --chip ${NVIDIA_CHIP}
END
    chmod +x doflash.sh
    tegraflash_custom_post
    rm -f ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.tegraflash.zip
    zip -r ${DEPLOY_DIR_IMAGE}/${IMAGE_NAME}.tegraflash.zip .
    ln -sf ${IMAGE_NAME}.tegraflash.zip ${DEPLOY_DIR_IMAGE}/${IMAGE_LINK_NAME}.tegraflash.zip
    cd $oldwd
}
create_tegraflash_pkg[vardepsexclude] += "DATETIME"

IMAGE_CMD_tegraflash = "create_tegraflash_pkg"
IMAGE_DEPENDS_tegraflash = "zip-native:do_populate_sysroot tegra-flashtools-native:do_populate_sysroot \
                            tegra-bootfiles:do_populate_sysroot ${IMAGE_UBOOT}:do_deploy"
IMAGE_TYPEDEP_tegraflash += "ext3"
