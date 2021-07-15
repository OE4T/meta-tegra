inherit image_types

IMAGE_TYPES += "tegraflash"

IMAGE_ROOTFS_ALIGNMENT ?= "4"

IMAGE_UBOOT ??= "u-boot"

DTBFILE ?= "${@os.path.basename(d.getVar('KERNEL_DEVICETREE', True).split()[0])}"
LNXFILE ?= "${IMAGE_UBOOT}-${MACHINE}.bin"
LNXSIZE ?= "67108864"

IMAGE_TEGRAFLASH_FS_TYPE ??= "ext4"
IMAGE_TEGRAFLASH_ROOTFS ?= "${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.${IMAGE_TEGRAFLASH_FS_TYPE}"
IMAGE_TEGRAFLASH_KERNEL ?= "${DEPLOY_DIR_IMAGE}/${LNXFILE}"

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

tegraflash_create_flash_config() {
    :
}

tegraflash_create_flash_config_tegra124() {
    local gptsize
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
    cat "${STAGING_DATADIR}/tegraflash/flash_${MACHINE}.cfg" | sed \
        -e"s,filename=fastboot.bin,filename=${IMAGE_UBOOT}-${MACHINE}.bin," \
        -e"s,#filename=fastboot.bin,filename=${IMAGE_UBOOT}-${MACHINE}.bin," \
        -e"/filename=boot.img/d" \
        -e"s,size=1073741824,size=${ROOTFSPART_SIZE}," \
        -e"s,#filename=tegra.dtb,filename=${DTBFILE}," \
        -e"s,size=2097152\s\+#BCTSIZE,size=${BOOTPART_SIZE}," \
        -e"s,size=8388608\s\+#PPTSIZE,size=$gptsize," \
        -e"s,#filename=ppt.img,filename=ppt.img," \
        -e"s,#filename=spt.img,filename=gpt.img," \
        > flash.cfg
}

# When using the Tegra210 boot redundancy feature, all of the
# bootloader binaries/data are combined into a bootfileset that is
# programmed into the boot0 and boot1 areas of the eMMC.  We
# size the "partitions" included in the BFS based on the actual
# size of the file, rather than allocating GPT partitions for them
# and sizing those to be at least the minimum allowed, but large
# enough to accommodate some growth.
tegraflash_create_flash_config_tegra210() {
    local destdir="$1"
    local gptsize="$2"
    local ebtsize=$(tegraflash_roundup_size cboot.bin)
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
        -e"s,EBTFILE,cboot.bin," -e"s,EBTSIZE,$ebtsize," \
        -e"s,LNXFILE,${LNXFILE}," \
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

tegraflash_create_flash_config_tegra186() {
    local destdir="$1"

    # The following sed expression are derived from xxx_TAG variables
    # in the L4T flash.sh script.  Tegra186-specific.
    cat "${STAGING_DATADIR}/tegraflash/flash_${MACHINE}.xml" | sed \
        -e"s,LNXFILE,${LNXFILE}," \
        -e"s,LNXSIZE,${LNXSIZE}," -e"s,LNXNAME,kernel," \
        -e"/SOSFILE/d" \
        -e"s,MB2TYPE,mb2_bootloader," -e"s,MB2FILE,nvtboot.bin," -e"s,MB2NAME,mb2," \
        -e"s,MPBTYPE,mts_preboot," -e"s,MPBFILE,preboot_d15_prod_cr.bin," -e"s,MPBNAME,mts-preboot," \
        -e"s,MBPTYPE,mts_bootpack," -e"s,MBPFILE,mce_mts_d15_prod_cr.bin," -e"s,MBPNAME,mts-bootpack," \
        -e"s,MB1TYPE,mb1_bootloader," -e"s,MB1FILE,mb1_prod.bin," -e"s,MB1NAME,mb1," \
        -e"s,BPFFILE,bpmp.bin," -e"s,BPFNAME,bpmp-fw," -e"s,BPFSIGN,true," \
        -e"s,DRAMECCFILE,dram-ecc.bin," -e"s,DRAMECCNAME,dram-ecc-fw," -e"s,DRAMECCTYPE,dram_ecc," \
        -e"s,BPFDTB-NAME,bpmp-fw-dtb," -e"s,BPMPDTB-SIGN,true," \
        -e"s,TBCFILE,cboot.bin," -e"s,TBCTYPE,bootloader," -e"s,TBCNAME,cpu-bootloader," \
        -e"s,TBCDTB-NAME,bootloader-dtb," -e"s,TBCDTB-FILE,${DTBFILE}," \
        -e"s,SCEFILE,camera-rtcpu-sce.bin," -e"s,SCENAME,sce-fw," -e"s,SCESIGN,true," \
        -e"s,SPEFILE,spe.bin," -e"s,SPENAME,spe-fw," -e"s,SPETYPE,spe_fw," \
        -e"s,WB0TYPE,WB0," -e"s,WB0FILE,warmboot.bin," -e"s,SC7NAME,sc7," \
        -e"s,TOSFILE,tos.img," -e"s,TOSNAME,secure-os," \
        -e"s,EKSFILE,eks.img," \
        -e"s,FBTYPE,data," -e"s,FBSIGN,false," -e"/FBFILE/d" \
	-e"s,KERNELDTB-NAME,kernel-dtb," -e"s,KERNELDTB-FILE,${DTBFILE}," \
	-e"s,APPFILE,${IMAGE_BASENAME}.img," -e"s,APPSIZE,${ROOTFSPART_SIZE}," \
	-e"s,PPTSIZE,2097152," \
        > flash.xml.in
}

BOOTFILES = ""
BOOTFILES_tegra210 = "\
    board_config_${MACHINE}.xml \
    cboot.bin \
    nvtboot_recovery.bin \
    nvtboot.bin \
    nvtboot_cpu.bin \
    warmboot.bin \
    bpmp.bin \
    tos.img \
"
BOOTFILES_tegra186 = "\
    bmp.blob \
    bpmp.bin \
    dram-ecc.bin \
    camera-rtcpu-sce.bin \
    cboot.bin \
    eks.img \
    mb1_prod.bin \
    mb1_recovery_prod.bin \
    mce_mts_d15_prod_cr.bin \
    nvtboot_cpu.bin \
    nvtboot_recovery.bin \
    nvtboot_recovery_cpu.bin \
    preboot_d15_prod_cr.bin \
    slot_metadata.bin \
    spe.bin \
    tos.img \
    nvtboot.bin \
    warmboot.bin \
    minimal_scr.cfg \
    mobile_scr.cfg \
    emmc.cfg \
"

create_tegraflash_pkg() {
    :
}

create_tegraflash_pkg_tegra124() {
    local gptsize
    PATH="${STAGING_BINDIR_NATIVE}/tegra124-flash:${PATH}"
    rm -rf "${WORKDIR}/tegraflash"
    mkdir -p "${WORKDIR}/tegraflash"
    oldwd=`pwd`
    cd "${WORKDIR}/tegraflash"
    ln -s "${STAGING_DATADIR}/tegraflash/${MACHINE}.cfg" .
    ln -s "${DEPLOY_DIR_IMAGE}/${IMAGE_UBOOT}-${MACHINE}.bin" .
    ln -s "${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}-${DTBFILE}" ./${DTBFILE}
    ln -s "${STAGING_DATADIR}/tegraflash/fastboot.bin" .
    ln -s "${STAGING_BINDIR_NATIVE}/tegra124-flash/nvflash" .
    tegraflash_custom_pre
    mksparse -v --fillpattern=0 "${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.${IMAGE_TEGRAFLASH_FS_TYPE}" system.img
    tegraflash_create_flash_config "${WORKDIR}/tegraflash" $gptsize
    mkgpt -c flash.cfg -P ppt.img -t ${EMMC_SIZE} -b ${BOOTPART_SIZE} -s 4KiB -a GPT -v GP1 -V
    rm -f doflash.sh
    cat > doflash.sh <<END
#!/bin/sh
./nvflash --bct ${MACHINE}.cfg --setbct --configfile flash.cfg --create --bl fastboot.bin --odmdata ${ODMDATA} --go
END
    chmod +x doflash.sh
    tegraflash_custom_post
    rm -f ${IMGDEPLOYDIR}/${IMAGE_NAME}.tegraflash.zip
    zip -r ${IMGDEPLOYDIR}/${IMAGE_NAME}.tegraflash.zip .
    ln -sf ${IMAGE_NAME}.tegraflash.zip ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.tegraflash.zip
    cd $oldwd
}

create_tegraflash_pkg_tegra210() {
    local gptsize
    PATH="${STAGING_BINDIR_NATIVE}/tegra210-flash:${PATH}"
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
    ln -s "${IMAGE_TEGRAFLASH_KERNEL}" ./${LNXFILE}
    ln -s "${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}-${DTBFILE}" ./${DTBFILE}
    for f in ${BOOTFILES}; do
        ln -s "${STAGING_DATADIR}/tegraflash/$f" .
    done
    ln -s "${STAGING_BINDIR_NATIVE}/tegra210-flash" .
    tegraflash_custom_pre
    mksparse -v --fillpattern=0 "${IMAGE_TEGRAFLASH_ROOTFS}" ${IMAGE_BASENAME}.img
    tegraflash_create_flash_config "${WORKDIR}/tegraflash" $gptsize
    if [ "${TEGRA210_REDUNDANT_BOOT}" != "1" ]; then
        mkgpt -c flash.xml -P ppt.img -t ${EMMC_SIZE} -b ${BOOTPART_SIZE} -s 4KiB -a GPT -v GP1 -V
    fi
    rm -f doflash.sh
    cat > doflash.sh <<END
#!/bin/sh
./tegra210-flash/tegraflash.py --bl cboot.bin --bct ${MACHINE}.cfg --odmdata ${ODMDATA} --bldtb ${DTBFILE} --applet nvtboot_recovery.bin \
              --boardconfig board_config_${MACHINE}.xml --cmd "flash;reboot" --cfg flash.xml --chip ${NVIDIA_CHIP}
END
    chmod +x doflash.sh
    tegraflash_custom_post
    rm -f ${IMGDEPLOYDIR}/${IMAGE_NAME}.tegraflash.zip
    zip -r ${IMGDEPLOYDIR}/${IMAGE_NAME}.tegraflash.zip .
    ln -sf ${IMAGE_NAME}.tegraflash.zip ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.tegraflash.zip
    cd $oldwd
}

create_tegraflash_pkg_tegra186() {
    local f
    PATH="${STAGING_BINDIR_NATIVE}/tegra186-flash:${PATH}"
    rm -rf "${WORKDIR}/tegraflash"
    mkdir -p "${WORKDIR}/tegraflash"
    oldwd=`pwd`
    cd "${WORKDIR}/tegraflash"
    ln -s "${STAGING_DATADIR}/tegraflash/${MACHINE}.cfg" .
    ln -s "${IMAGE_TEGRAFLASH_KERNEL}" ./${LNXFILE}
    ln -s "${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}-${DTBFILE}" ./${DTBFILE}
    for f in ${BOOTFILES}; do
        ln -s "${STAGING_DATADIR}/tegraflash/$f" .
    done
    for f in ${STAGING_DATADIR}/tegraflash/tegra186-*.cfg; do
	ln -s $f .
    done
    for f in ${STAGING_DATADIR}/tegraflash/tegra186-a02-bpmp*.dtb; do
	ln -s $f .
    done
    ln -s ${STAGING_BINDIR_NATIVE}/tegra186-flash .
    tegraflash_custom_pre
    mksparse -v --fillpattern=0 "${IMAGE_TEGRAFLASH_ROOTFS}" ${IMAGE_BASENAME}.img
    tegraflash_create_flash_config "${WORKDIR}/tegraflash"
    rm -f doflash.sh
    cat > doflash.sh <<END
#!/bin/sh
PATH=\$PATH:tegra186-flash
./tegra186-flash/tegra186-flash-helper.sh flash.xml.in ${DTBFILE} ${MACHINE}.cfg ${ODMDATA}
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
do_image_tegraflash[depends] += "zip-native:do_populate_sysroot \
                                 ${SOC_FAMILY}-flashtools-native:do_populate_sysroot \
                                 tegra-bootfiles:do_populate_sysroot tegra-bootfiles:do_populate_lic \
                                 ${@d.expand('${IMAGE_UBOOT}:do_deploy ${IMAGE_UBOOT}:do_populate_lic') if d.getVar('IMAGE_UBOOT') != '' else  ''}"
IMAGE_TYPEDEP_tegraflash += "${IMAGE_TEGRAFLASH_FS_TYPE}"
