inherit image_types image_types_cboot pythonnative perlnative

IMAGE_TYPES += "tegraflash"

IMAGE_ROOTFS_ALIGNMENT ?= "4"

IMAGE_UBOOT ??= "u-boot"
INITRD_IMAGE ??= ""
KERNEL_ARGS ??= ""

DTBFILE ?= "${@os.path.basename(d.getVar('KERNEL_DEVICETREE').split()[0])}"
LNXFILE ?= "${@'${IMAGE_UBOOT}-${MACHINE}.bin' if '${IMAGE_UBOOT}' != '' else '${INITRD_IMAGE}-${MACHINE}.cboot'}"
LNXSIZE ?= "67108864"

IMAGE_TEGRAFLASH_FS_TYPE ??= "ext4"
IMAGE_TEGRAFLASH_ROOTFS ?= "${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.${IMAGE_TEGRAFLASH_FS_TYPE}"
IMAGE_TEGRAFLASH_KERNEL ?= "${DEPLOY_DIR_IMAGE}/${LNXFILE}"

BL_IS_CBOOT = "${@'1' if d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('cboot') else '0'}"
TEGRA_SPIFLASH_BOOT ??= ""

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
    local destdir="$1"
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
        > $destdir/flash.cfg
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
    local bpfsize=$(tegraflash_roundup_size sc7entry-firmware.bin)
    local wb0size=$(tegraflash_roundup_size warmboot.bin)
    local tossize=$(tegraflash_roundup_size tos-mon-only.img)
    # Total size of the bootfileset cannot exceed ((size of one boot area) - 1MiB) / 2,
    # (1.5MiB on the eMMC shipped on the TX1 SOM).
    if [ "${TEGRA210_REDUNDANT_BOOT}" = "1" -a -n "${BOOTPART_SIZE}" ]; then
        local bfssize=$(expr $ebtsize + $nvcsize + $tbcsize + $dtbsize + $dtbsize + $bpfsize + $wb0size + $tossize)
	local bfsmax=$(expr \( ${BOOTPART_SIZE} / 2 - 1048576 \) / 2)
        if [ $bfssize -gt $bfsmax ]; then
            bberror "BFS actual size ($bfssize) larger than allocation ($bfsmax)"
            exit 1
        fi
    fi
    head -n 1 ${STAGING_DATADIR}/nv_tegra/nv_tegra_release > ./nv_tegra_release
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
        -e"s,BXF,BPF," -e"s,BPFFILE,sc7entry-firmware.bin," -e"s,BPFSIZE,$bpfsize," \
        -e"/BPFDTB-FILE/d" \
        -e"s,WX0,WB0," -e"s,WB0TYPE,WB0," -e"s,WB0FILE,warmboot.bin," -e"s,WB0SIZE,$wb0size," \
        -e"s,TXS,TOS," -e"s,TOSFILE,tos-mon-only.img," -e"s,TOSSIZE,$tossize," \
        -e"s,EXS,EKS," -e"s,EKSFILE,eks.img," \
        -e"s,FBTYPE,data," -e"/FBFILE/d" \
        -e"s,DXB,DTB," -e"s,DTBFILE,${DTBFILE}," -e"s,DTBSIZE,$dtbsize," \
        -e"s,APPFILE,${IMAGE_BASENAME}.img," -e"s,APPSIZE,${ROOTFSPART_SIZE}," \
        -e"s,TXC,TBC," -e"s,TBCTYPE,bootloader," -e"s,TBCFILE,nvtboot_cpu.bin," -e"s,TBCSIZE,$tbcsize,"  \
        -e"s,EFISIZE,67108864," -e"/EFIFILE/d" \
        -e"s,BCTSIZE,${BOOTPART_SIZE}," -e"s,PPTSIZE,$gptsize," \
        -e"s,PPTFILE,ppt.img," -e"s,GPTFILE,gpt.img," \
        > $destdir/flash.xml.in
    [ "${TEGRA_SPIFLASH_BOOT}" = "1" ] || return 0
    cat "${STAGING_DATADIR}/tegraflash/sdcard_${MACHINE}.xml" | sed \
        -e"s,EBTFILE,cboot.bin," -e"s,EBTSIZE,$ebtsize," \
        -e"s,LNXFILE,${LNXFILE}," \
        -e"/NCTFILE/d" -e"s,NCTTYPE,data," \
        -e"/SOSFILE/d" \
        -e"s,NXC,NVC," -e"s,NVCTYPE,bootloader," -e"s,NVCFILE,nvtboot.bin," -e "s,NVCSIZE,$nvcsize," \
        -e"s,MPBTYPE,data," -e"/MPBFILE/d" \
        -e"s,MBPTYPE,data," -e"/MBPFILE/d" \
        -e"s,BXF,BPF," -e"s,BPFFILE,sc7entry-firmware.bin," -e"s,BPFSIZE,$bpfsize," \
        -e"/BPFDTB-FILE/d" \
        -e"s,WX0,WB0," -e"s,WB0TYPE,WB0," -e"s,WB0FILE,warmboot.bin," -e"s,WB0SIZE,$wb0size," \
        -e"s,TXS,TOS," -e"s,TOSFILE,tos-mon-only.img," -e"s,TOSSIZE,$tossize," \
        -e"s,EXS,EKS," -e"s,EKSFILE,eks.img," \
        -e"s,FBTYPE,data," -e"/FBFILE/d" \
        -e"s,DXB,DTB," -e"s,DTBFILE,${DTBFILE}," -e"s,DTBSIZE,$dtbsize," \
        -e"s,APPFILE,${IMAGE_BASENAME}.img," -e"s,APPSIZE,${ROOTFSPART_SIZE}," \
        -e"s,TXC,TBC," -e"s,TBCTYPE,bootloader," -e"s,TBCFILE,nvtboot_cpu.bin," -e"s,TBCSIZE,$tbcsize,"  \
        -e"s,EFISIZE,67108864," -e"/EFIFILE/d" \
        -e"s,BCTSIZE,${BOOTPART_SIZE}," -e"s,PPTSIZE,$gptsize," \
        -e"s,PPTFILE,ppt.img," -e"s,GPTFILE,gpt.img," \
        > $destdir/sdcard.xml.in
    cat "${STAGING_DATADIR}/tegraflash/sdcard-layout.in" | sed \
        -e"s,DTBFILE,${DTBFILE}.encrypt," \
        -e"s,LNXFILE,${LNXFILE}.encrypt," \
        -e"s,APPFILE,${IMAGE_BASENAME}.raw.img," \
      > $destdir/sdcard-layout
}

tegraflash_create_flash_config_tegra186() {
    local destdir="$1"
    local lnxfile="$2"

    # The following sed expression are derived from xxx_TAG variables
    # in the L4T flash.sh script.  Tegra186-specific.
    cat "${STAGING_DATADIR}/tegraflash/flash_${MACHINE}.xml" | sed \
        -e"s,LNXFILE,$lnxfile," \
        -e"s,LNXSIZE,${LNXSIZE}," -e"s,LNXNAME,kernel," \
        -e"/SOSFILE/d" \
        -e"s,MB2TYPE,mb2_bootloader," -e"s,MB2FILE,nvtboot.bin," -e"s,MB2NAME,mb2," \
        -e"s,MPBTYPE,mts_preboot," -e"s,MPBFILE,preboot_d15_prod_cr.bin," -e"s,MPBNAME,mts-preboot," \
        -e"s,MBPTYPE,mts_bootpack," -e"s,MBPFILE,mce_mts_d15_prod_cr.bin," -e"s,MBPNAME,mts-bootpack," \
        -e"s,MB1TYPE,mb1_bootloader," -e"s,MB1FILE,mb1_prod.bin," -e"s,MB1NAME,mb1," \
        -e"s,DRAMECCTYPE,dram_ecc," -e"s,DRAMECCFILE,dram-ecc.bin," -e"s,DRAMECCNAME,dram-ecc-fw," \
        -e"s,BADPAGETYPE,black_list_info," -e"s,BADPAGEFILE,badpage.bin," -e"s,BADPAGENAME,badpage-fw," \
        -e"s,BPFFILE,bpmp.bin," -e"s,BPFNAME,bpmp-fw," -e"s,BPFSIGN,true," \
        -e"s,BPFDTB-NAME,bpmp-fw-dtb," -e"s,BPMPDTB-SIGN,true," \
        -e"s,TBCFILE,cboot.bin," -e"s,TBCTYPE,bootloader," -e"s,TBCNAME,cpu-bootloader," \
        -e"s,TBCDTB-NAME,bootloader-dtb," -e"s,TBCDTB-FILE,${DTBFILE}," \
        -e"s,SCEFILE,camera-rtcpu-sce.img," -e"s,SCENAME,sce-fw," -e"s,SCESIGN,true," \
        -e"s,SPEFILE,spe.bin," -e"s,SPENAME,spe-fw," -e"s,SPETYPE,spe_fw," \
        -e"s,WB0TYPE,WB0," -e"s,WB0FILE,warmboot.bin," -e"s,SC7NAME,sc7," \
        -e"s,TOSFILE,tos-mon-only.img," -e"s,TOSNAME,secure-os," \
        -e"s,EKSFILE,eks.img," \
        -e"s,FBTYPE,data," -e"s,FBSIGN,false," -e"/FBFILE/d" \
	-e"s,KERNELDTB-NAME,kernel-dtb," -e"s,KERNELDTB-FILE,${DTBFILE}," \
	-e"s,APPFILE,${IMAGE_BASENAME}.img," -e"s,APPSIZE,${ROOTFSPART_SIZE}," \
	-e"s,PPTSIZE,2097152," \
        > $destdir/flash.xml.in
}

tegraflash_create_flash_config_tegra194() {
    local destdir="$1"
    local lnxfile="$2"

    # The following sed expression are derived from xxx_TAG variables
    # in the L4T flash.sh script.  Tegra194-specific.
    # Note that the blank before DTB_FILE is important, to
    # prevent BPFDTB_FILE from being matched.
    cat "${STAGING_DATADIR}/tegraflash/flash_${MACHINE}.xml" | sed \
        -e"s,LNXFILE,$lnxfile," -e"s,LNXSIZE,${LNXSIZE}," \
        -e"s,TEGRABOOT,nvtboot_t194.bin," \
        -e"s,MTSPREBOOT,preboot_c10_prod_cr.bin," \
        -e"s,MTS_MCE,mce_c10_prod_cr.bin," \
        -e"s,MTSPROPER,mts_c10_prod_cr.bin," \
        -e"s,MB1FILE,mb1_t194_prod.bin," \
        -e"s,BPFFILE,bpmp_t194.bin," \
        -e"s,TBCFILE,cboot_t194.bin," \
        -e"s,TBCDTB-FILE,${DTBFILE}," \
        -e"s,CAMERAFW,camera-rtcpu-rce.img," \
        -e"s,SPEFILE,spe_t194.bin," \
        -e"s,WB0BOOT,warmboot_t194_prod.bin," \
        -e"s,TOSFILE,tos-mon-only_t194.img," \
        -e"s,EKSFILE,eks.img," \
	-e"s, DTB_FILE, ${DTBFILE}," \
	-e"s,CBOOTOPTION_FILE,cbo.dtb," \
	-e"s,APPFILE,${IMAGE_BASENAME}.img," -e"s,APPSIZE,${ROOTFSPART_SIZE}," \
        > $destdir/flash.xml.in
}

BOOTFILES = ""
BOOTFILES_tegra210 = "\
    bmp.blob \
    cboot.bin \
    eks.img \
    nvtboot_recovery.bin \
    nvtboot.bin \
    nvtboot_cpu.bin \
    warmboot.bin \
    rp4.blob \
    sc7entry-firmware.bin \
    tos-mon-only.img \
"
BOOTFILES_tegra186 = "\
    adsp-fw.bin \
    bmp.blob \
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
    slot_metadata.bin \
    spe.bin \
    tos-mon-only.img \
    nvtboot.bin \
    warmboot.bin \
    minimal_scr.cfg \
    mobile_scr.cfg \
    emmc.cfg \
"

BOOTFILES_tegra194 = "\
    adsp-fw.bin \
    bmp.blob \
    bpmp_t194.bin \
    camera-rtcpu-rce.img \
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
    slot_metadata.bin \
    spe_t194.bin \
    tos-mon-only_t194.img \
    warmboot_t194_prod.bin \
    xusb_sil_rel_fw \
    cbo.dtb \
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
    ln -s "${DEPLOY_DIR_IMAGE}/${DTBFILE}" ./${DTBFILE}
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
    local gptsize=16896
    PATH="${STAGING_BINDIR_NATIVE}/tegra210-flash:${PATH}"
    if [ "${TEGRA210_REDUNDANT_BOOT}" != "1" -a -n "${BOOTPART_SIZE}" ]; then
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
    cp "${DEPLOY_DIR_IMAGE}/${DTBFILE}" ./${DTBFILE}
    if [ -n "${KERNEL_ARGS}" ]; then
        fdtput -t s ./${DTBFILE} /chosen bootargs "${KERNEL_ARGS}"
    else
        fdtput -d ./${DTBFILE} /chosen bootargs
    fi
    for f in ${BOOTFILES}; do
        ln -s "${STAGING_DATADIR}/tegraflash/$f" .
    done
    if [ -n "${NVIDIA_BOARD_CFG}" ]; then
        ln -s "${STAGING_DATADIR}/tegraflash/board_config_${MACHINE}.xml" .
	boardcfg=board_config_${MACHINE}.xml
    else
	boardcfg=
    fi

    ln -s "${STAGING_BINDIR_NATIVE}/tegra210-flash" .
    tegraflash_custom_pre
    if [ "${TEGRA_SPIFLASH_BOOT}" != "1" ]; then
        mksparse -b ${TEGRA_BLBLOCKSIZE} -v --fillpattern=0 "${IMAGE_TEGRAFLASH_ROOTFS}" ${IMAGE_BASENAME}.img
    fi
    tegraflash_create_flash_config "${WORKDIR}/tegraflash" $gptsize
    if [ "${TEGRA210_REDUNDANT_BOOT}" != "1" -a -n "${EMMC_SIZE}" -a -n "${BOOTPART_SIZE}" ]; then
        ln -sf flash.xml.in flash.xml
        mkgpt -c flash.xml -P ppt.img -t ${EMMC_SIZE} -b ${BOOTPART_SIZE} -s 4KiB -a GPT -v GP1 -V
	rm -f flash.xml
    fi
    if [ "${TEGRA_SPIFLASH_BOOT}" = "1" ]; then
        BOARDID=${TEGRA_BOARDID} FAB=${TEGRA_FAB} tegra210-flash-helper.sh --sign ./sdcard.xml.in ${DTBFILE} ${MACHINE}.cfg ${ODMDATA} "$boardcfg"
	dd if=/dev/zero of=${IMGDEPLOYDIR}/${IMAGE_NAME}.sdcard bs=1 count=0 seek=${TEGRAFLASH_SDCARD_SIZE}
        sgdisk ${IMGDEPLOYDIR}/${IMAGE_NAME}.sdcard --clear
        ln -s "${IMAGE_TEGRAFLASH_ROOTFS}" ./${IMAGE_BASENAME}.raw.img
        while IFS=, read partnum partname partsize partfile; do
	    if [ $partsize -eq 0 ]; then
	        partarg="--largest-new=$partnum"
	    else
                partarg="--new=$partnum:0:+$(expr \( $partsize + 511 \) / 512)"
	    fi
	    sgdisk ${IMGDEPLOYDIR}/${IMAGE_NAME}.sdcard $partarg --typecode=$partnum:8300 -c $partnum:$partname
	done < sdcard-layout
        sgdisk ${IMGDEPLOYDIR}/${IMAGE_NAME}.sdcard --verify
	blksize=${@int(d.getVar('IMAGE_ROOTFS_ALIGNMENT')) * 1024}
	startpoint=$blksize
        while IFS=, read partnum partname partsize partfile; do
	    if [ -e signed/$partfile ]; then
	        partfile=signed/$partfile
	    fi
	    dd if=$partfile of=${IMGDEPLOYDIR}/${IMAGE_NAME}.sdcard conv=notrunc,fsync seek=$(expr $startpoint / $blksize) bs=$blksize
	    startpoint=$(expr $startpoint + \( \( $partsize + \( $blksize - 1 \) \) / $blksize \) \* $blksize)
	done < sdcard-layout
        sgdisk ${IMGDEPLOYDIR}/${IMAGE_NAME}.sdcard --verify
	rm ${IMAGE_BASENAME}.raw.img
	rm -r signed
	ln -sf ${IMAGE_NAME}.sdcard ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.sdcard
    fi

    rm -f doflash.sh
    cat > doflash.sh <<END
#!/bin/sh
PATH=\$PATH:tegra210-flash
./tegra210-flash/tegra210-flash-helper.sh flash.xml.in ${DTBFILE} ${MACHINE}.cfg ${ODMDATA} "$boardcfg" "${TEGRA_SPIFLASH_BOOT}"
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
    cp "${DEPLOY_DIR_IMAGE}/${DTBFILE}" ./${DTBFILE}
    if [ "${BL_IS_CBOOT}" = "1" -a -n "${KERNEL_ARGS}" ]; then
        fdtput -t s ./${DTBFILE} /chosen bootargs "${KERNEL_ARGS}"
    else
        fdtput -d ./${DTBFILE} /chosen bootargs
    fi
    ln -sf "${DEPLOY_DIR_IMAGE}/cboot-${MACHINE}.bin" ./cboot.bin
    for f in ${BOOTFILES}; do
        ln -s "${STAGING_DATADIR}/tegraflash/$f" .
    done
    cp ${STAGING_DATADIR}/tegraflash/flashvars .
    . ./flashvars
    for var in $FLASHVARS; do
	eval pat=$`echo $var`
	if [ -z "$pat" ]; then
	    echo "ERR: missing variable: $var" >&2
	    exit 1
        fi
	fnglob=`echo $pat | sed -e"s,@BPFDTBREV@,\*," -e"s,@BOARDREV@,\*," -e"s,@PMICREV@,\*," -e"s,@CHIPREV@,\*,"`
	for fname in ${STAGING_DATADIR}/tegraflash/$fnglob; do
	    if [ ! -e $fname ]; then
	       bbfatal "$var file(s) not found"
	    fi
	    ln -sf $fname ./
	done
    done
    ln -s ${STAGING_BINDIR_NATIVE}/tegra186-flash .
    dd if=/dev/zero of=badpage.bin bs=4096 count=1
    tegraflash_custom_pre
    mksparse -v --fillpattern=0 "${IMAGE_TEGRAFLASH_ROOTFS}" ${IMAGE_BASENAME}.img
    tegraflash_create_flash_config "${WORKDIR}/tegraflash" ${LNXFILE}
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

create_tegraflash_pkg_tegra194() {
    local f
    PATH="${STAGING_BINDIR_NATIVE}/tegra186-flash:${PATH}"
    rm -rf "${WORKDIR}/tegraflash"
    mkdir -p "${WORKDIR}/tegraflash"
    oldwd=`pwd`
    cd "${WORKDIR}/tegraflash"
    ln -s "${STAGING_DATADIR}/tegraflash/${MACHINE}.cfg" .
    ln -s "${STAGING_DATADIR}/tegraflash/${MACHINE}-override.cfg" .
    ln -s "${IMAGE_TEGRAFLASH_KERNEL}" ./${LNXFILE}
    if [ -n "${KERNEL_ARGS}" ]; then
        cp "${DEPLOY_DIR_IMAGE}/${DTBFILE}" ./${DTBFILE}
        bootargs="`fdtget ./${DTBFILE} /chosen bootargs 2>/dev/null`"
        fdtput -t s ./${DTBFILE} /chosen bootargs "$bootargs ${KERNEL_ARGS}"
    else
        ln -s "${DEPLOY_DIR_IMAGE}/${DTBFILE}" ./${DTBFILE}
    fi
    ln -sf "${DEPLOY_DIR_IMAGE}/cboot-${MACHINE}.bin" ./cboot_t194.bin
    for f in ${BOOTFILES}; do
        ln -s "${STAGING_DATADIR}/tegraflash/$f" .
    done
    cp ${STAGING_DATADIR}/tegraflash/flashvars .
    for f in ${STAGING_DATADIR}/tegraflash/tegra19[4x]-*.cfg; do
	ln -s $f .
    done
    for f in ${STAGING_DATADIR}/tegraflash/tegra194-*-bpmp-*.dtb; do
	ln -s $f .
    done
    ln -s ${STAGING_BINDIR_NATIVE}/tegra186-flash .
    tegraflash_custom_pre
    mksparse -v --fillpattern=0 "${IMAGE_TEGRAFLASH_ROOTFS}" ${IMAGE_BASENAME}.img
    tegraflash_create_flash_config "${WORKDIR}/tegraflash" ${LNXFILE}
    rm -f doflash.sh
    cat > doflash.sh <<END
#!/bin/sh
PATH=\$PATH:tegra186-flash
./tegra186-flash/tegra194-flash-helper.sh flash.xml.in ${DTBFILE} ${MACHINE}.cfg,${MACHINE}-override.cfg ${ODMDATA}
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
do_image_tegraflash[depends] += "zip-native:do_populate_sysroot dtc-native:do_populate_sysroot \
                                 ${SOC_FAMILY}-flashtools-native:do_populate_sysroot gptfdisk-native:do_populate_sysroot \
                                 tegra-bootfiles:do_populate_sysroot tegra-bootfiles:do_populate_lic \
                                 virtual/kernel:do_deploy \
                                 ${@'${INITRD_IMAGE}:do_image_complete' if d.getVar('INITRD_IMAGE') != '' else  ''} \
                                 ${@'${IMAGE_UBOOT}:do_deploy ${IMAGE_UBOOT}:do_populate_lic' if d.getVar('IMAGE_UBOOT') != '' else  ''}"
IMAGE_TYPEDEP_tegraflash += "${IMAGE_TEGRAFLASH_FS_TYPE}"

oe_make_bup_payload() {
    bbfatal "BUP payloads only supported on tegra186/tegra194 platforms"
}

oe_make_bup_payload_tegra186() {
    export cbootfilename=cboot.bin
    oe_make_bup_payload_common "$@"
}

oe_make_bup_payload_tegra194() {
    export cbootfilename=cboot_t194.bin
    oe_make_bup_payload_common "$@"
}
oe_make_bup_payload_common() {
    PATH="${STAGING_BINDIR_NATIVE}/tegra186-flash:${PATH}"
    rm -rf ${WORKDIR}/bup-payload
    mkdir ${WORKDIR}/bup-payload
    oldwd="$PWD"
    cd ${WORKDIR}/bup-payload
    if [ "${SOC_FAMILY}" = "tegra186" ]; then
        dd if=/dev/zero of=badpage.bin bs=4096 count=1
    fi
    # BUP generator really wants to use 'boot.img' for the LNX
    # partition contents
    ln -sf $1 ./boot.img
    tegraflash_create_flash_config "${WORKDIR}/bup-payload" boot.img
    # XXX put back the APPFILE placeholder for later use - not used for signing
    sed -i -e's,${IMAGE_BASENAME}.img,APPFILE,' flash.xml.in
    ln -sf "${STAGING_DATADIR}/nv_tegra/nv_tegra_release" .
    ln -s "${STAGING_DATADIR}/tegraflash/${MACHINE}.cfg" .
    if [ "${SOC_FAMILY}" = "tegra194" ]; then
        ln -s "${STAGING_DATADIR}/tegraflash/${MACHINE}-override.cfg" .
    fi
    rm -f ./${DTBFILE}
    cp "${DEPLOY_DIR_IMAGE}/${DTBFILE}" ./${DTBFILE}
    if [ "${BL_IS_CBOOT}" = "1" -a -n "${KERNEL_ARGS}" ]; then
        fdtput -t s ./${DTBFILE} /chosen bootargs "${KERNEL_ARGS}"
    else
        fdtput -d ./${DTBFILE} /chosen bootargs
    fi
    ln -s "${DEPLOY_DIR_IMAGE}/cboot-${MACHINE}.bin" ./$cbootfilename
    for f in ${BOOTFILES}; do
        ln -s "${STAGING_DATADIR}/tegraflash/$f" .
    done
    cp ${STAGING_DATADIR}/tegraflash/flashvars .
    . ./flashvars
    for var in $FLASHVARS; do
	eval pat=$`echo $var`
	if [ -z "$pat" ]; then
	    echo "ERR: missing variable: $var" >&2
	    exit 1
        fi
	if [ "${SOC_FAMILY}" = "tegra186" ]; then
	    fnglob=`echo $pat | sed -e"s,@BPFDTBREV@,\*," -e"s,@BOARDREV@,\*," -e"s,@PMICREV@,\*," -e"s,@CHIPREV@,\*,"`
	    for fname in ${STAGING_DATADIR}/tegraflash/$fnglob; do
	        if [ ! -e $fname ]; then
		    bbfatal "$var file(s) not found"
		fi
	        ln -sf $fname ./
	    done
	else
	    for f in ${STAGING_DATADIR}/tegraflash/tegra19[4x]-*.cfg; do
		ln -sf $f .
	    done
	    for f in ${STAGING_DATADIR}/tegraflash/tegra194-*-bpmp-*.dtb; do
		ln -sf $f .
	    done
	fi
    done
    rm -f ./slot_metadata.bin
    cp ${STAGING_DATADIR}/tegraflash/slot_metadata.bin ./
    mkdir ./rollback
    ln -sf ${STAGING_BINDIR_NATIVE}/tegra186-flash/rollback_parser.py ./rollback/
    ln -snf ${STAGING_DATADIR}/nv_tegra/rollback/t${@d.getVar('NVIDIA_CHIP')[2:]}x ./rollback/
    ln -sf ${STAGING_BINDIR_NATIVE}/tegra186-flash/BUP_generator.py ./
    ln -sf ${STAGING_BINDIR_NATIVE}/tegra186-flash/${SOC_FAMILY}-flash-helper.sh ./
    sed -e 's,^function ,,' ${STAGING_BINDIR_NATIVE}/tegra186-flash/l4t_bup_gen.func > ./l4t_bup_gen.func
    rm -rf signed
    export BOARDID=${TEGRA_BOARDID}
    export FAB=${TEGRA_FAB}
    export fuselevel=fuselevel_production
    export localbootfile=${LNXFILE}
    if [ "${SOC_FAMILY}" = "tegra194" ]; then
        export CHIPREV=${TEGRA_CHIPREV}
        sdramcfg=${MACHINE}.cfg,${MACHINE}-override.cfg
    else
        sdramcfg=${MACHINE}.cfg
    fi
    ./${SOC_FAMILY}-flash-helper.sh --bup ./flash.xml.in ${DTBFILE} $sdramcfg ${ODMDATA}
    cd "$oldwd"
}

create_bup_payload_image() {
    local type="$1"
    oe_make_bup_payload ${IMGDEPLOYDIR}/${IMAGE_NAME}.rootfs.${type}
    install -m 0644 ${WORKDIR}/bup-payload/bl_update_payload ${IMGDEPLOYDIR}/${IMAGE_NAME}.bup-payload
    ln -sf ${IMAGE_NAME}.bup-payload ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.bup-payload
}
create_bup_payload_image[vardepsexclude] += "DATETIME"

CONVERSIONTYPES += "bup-payload"
CONVERSION_DEPENDS_bup-payload = "tegra186-flashtools-native tegra-bootfiles tegra186-redundant-boot nv-tegra-release dtc-native virtual/bootloader:do_deploy virtual/kernel:do_deploy"
CONVERSION_CMD_bup-payload = "create_bup_payload_image ${type}"
IMAGE_TYPES += "cpio.gz.cboot.bup-payload"
