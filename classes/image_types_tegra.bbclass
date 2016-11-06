inherit image_types

IMAGE_TYPES += "tegraflash"

IMAGE_ROOTFS_ALIGNMENT ?= "4096"

IMAGE_UBOOT ??= "u-boot"

DTBFILE ?= "${@os.path.basename(d.getVar('KERNEL_DEVICETREE', True).split()[0])}"

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

tegraflash_roundup_size() {
    local actsize=$(stat -L -c "%s" "$1")
    local blks=$(expr \( $actsize + 4095 \) / 4096)
    expr $blks \* 4096
}

# When using the Tegra210 boot redundancy feature, all of the
# bootloader binaries/data are combined into a bootfileset that is
# programmed into the boot0 and boot1 areas of the eMMC.  We
# size the "partitions" included in the BFS based on the actual
# size of the file, rather than allocating GPT partitions for them
# and sizing those to be at least the minimum allowed, but large
# enough to accommodate some growth.
tegraflash_create_flash_config() {
    local destdir="$1"
    local gptsize="$2"
    local ebtsize=$(tegraflash_roundup_size ${IMAGE_UBOOT}-${MACHINE}.bin)
    local nvcsize=$(tegraflash_roundup_size nvtboot.bin)
    local tbcsize=$(tegraflash_roundup_size nvtboot_cpu.bin)
    local dtbsize=$(tegraflash_roundup_size ${DTBFILE})
    local bpfsize=$(tegraflash_roundup_size bpmp.bin)
    local wb0size=$(tegraflash_roundup_size warmboot.bin)
    local tossize=$(tegraflash_roundup_size tos.img)
    # Total size of the bootfileset cannot exceed ((size of one boot area) - 1MiB) / 2,
    # (1.5MiB on the eMMC shipped on the TX1 SOM).
    if [ "${TEGRA210_REDUNDANT_BOOT}" = "1" ]; then
        local bfssize=$(expr $ebtsize + $nvcsize + $tbcsize + $dtbsize + $dtbsize + $bpfsize + $wb0size + $tossize)
	local bfsmax=$(expr \( ${BOOTPART_SIZE} / 2 - 1048576 \) / 2)
        if [ $bfssize -gt $bfsmax ]; then
            bberror "BFS actual size ($bfssize) larger than allocation ($bfsmax)"
            exit 1
        fi
    fi
    # The following sed expression are derived from xxx_TAG variables
    # in the L4T flash.sh script.  Some of the substitutions apply only to
    # the redundant-boot layout, some apply only to the non-redundant layout.
    # Note that these are for Tegra210 based systems only.
    cat "${STAGING_DATADIR}/tegraflash/flash_${MACHINE}.xml" | sed \
        -e"s,EBTFILE,${IMAGE_UBOOT}-${MACHINE}.bin," -e"s,EBTSIZE,$ebtsize," \
        -e"/LNXFILE/d" \
        -e"/NCTFILE/d" -e"s,NCTTYPE,data," \
        -e"/SOSFILE/d" \
        -e"s,NXC,NVC," -e"s,NVCTYPE,bootloader," -e"s,NVCFILE,nvtboot.bin," -e "s,NVCSIZE,$nvcsize," \
        -e"s,MPBTYPE,data," -e"/MPBFILE/d" \
        -e"s,MBPTYPE,data," -e"/MBPFILE/d" \
        -e"s,BXF,BPF," -e"s,BPFFILE,bpmp.bin," -e"s,BPFSIZE,$bpfsize," \
        -e"s,WX0,WB0," -e"s,WB0TYPE,WB0," -e"s,WB0FILE,warmboot.bin," -e"s,WB0SIZE,$wb0size," \
        -e"s,TXS,TOS," -e"s,TOSFILE,tos.img," -e"s,TOSSIZE,$tossize," \
        -e"/EKSFILE/d" \
        -e"s,FBTYPE,data," -e"/FBFILE/d" \
        -e"s,DXB,DTB," -e"s,DTBFILE,${DTBFILE}," -e"s,DTBSIZE,$dtbsize," \
        -e"s,APPFILE,${IMAGE_BASENAME}.img," -e"s,APPSIZE,${ROOTFSPART_SIZE}," \
        -e"s,TXC,TBC," -e"s,TBCTYPE,bootloader," -e"s,TBCFILE,nvtboot_cpu.bin," -e"s,TBCSIZE,$tbcsize,"  \
        -e"s,EFISIZE,67108864," -e"/EFIFILE/d" \
        -e"s,BCTSIZE,${BOOTPART_SIZE}," -e"s,PPTSIZE,$gptsize," \
        -e"s,PPTFILE,ppt.img," -e"s,GPTFILE,gpt.img," \
        > flash.xml
}

create_tegraflash_pkg() {
    local gptsize
    if [ "${TEGRA210_REDUNDANT_BOOT}" != "1" ]; then
        if [ `expr ${BOOTPART_LIMIT} % ${EMMC_DEVSECT_SIZE}` -ne 0 ]; then
            bberror "Boot partition limit must be an even multiple of the device sector size"
            exit 1
        elif [ `expr ${BOOTPART_SIZE} % ${EMMC_DEVSECT_SIZE}` -ne 0 ]; then
            bberror "Boot partition size must be an even multiple of the device sector size"
            exit 1
        fi
        gptsize=`expr ${BOOTPART_LIMIT} - ${BOOTPART_SIZE}`
        if [ $gptsize -lt ${EMMC_DEVSECT_SIZE} ]; then
            bberror "No space for primary GPT"
            exit 1
        fi
    fi
    rm -rf "${WORKDIR}/tegraflash"
    mkdir -p "${WORKDIR}/tegraflash"
    oldwd=`pwd`
    cd "${WORKDIR}/tegraflash"
    ln -s "${STAGING_DATADIR}/tegraflash/${MACHINE}.cfg" .
    ln -s "${DEPLOY_DIR_IMAGE}/${IMAGE_UBOOT}-${MACHINE}.bin" .
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
    mksparse -v --fillpattern=0 "${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.ext3" ${IMAGE_BASENAME}.img
    tegraflash_create_flash_config "${WORKDIR}/tegraflash" $gptsize
    if [ "${TEGRA210_REDUNDANT_BOOT}" != "1" ]; then
        mkgpt -c flash.xml -P ppt.img -t ${EMMC_SIZE} -b ${BOOTPART_SIZE} -s 4KiB -a GPT -v GP1 -V
    fi
    rm -f doflash.sh
    cat > doflash.sh <<END
#!/bin/sh
tegraflash.py --bl cboot.bin --bct ${MACHINE}.cfg --odmdata ${ODMDATA} --bldtb ${DTBFILE} --applet nvtboot_recovery.bin \
              --boardconfig board_config_${MACHINE}.xml --cmd "flash;reboot" --cfg flash.xml --chip ${NVIDIA_CHIP}
END
    chmod +x doflash.sh
    tegraflash_custom_post
    rm -f ${IMGDEPLOYDIR}/${IMAGE_NAME}.tegraflash.zip
    zip -r ${IMGDEPLOYDIR}/${IMAGE_NAME}.tegraflash.zip .
    ln -sf ${IMAGE_NAME}.tegraflash.zip ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.tegraflash.zip
    cd $oldwd
}
create_tegraflash_pkg[vardepsexclude] += "DATETIME"

IMAGE_CMD_tegraflash = "create_tegraflash_pkg"
IMAGE_DEPENDS_tegraflash = "zip-native:do_populate_sysroot tegra-flashtools-native:do_populate_sysroot \
                            tegra-bootfiles:do_populate_sysroot ${IMAGE_UBOOT}:do_deploy"
IMAGE_TYPEDEP_tegraflash += "ext3"
