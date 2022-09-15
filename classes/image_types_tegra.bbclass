inherit image_types image_types_cboot image_types_tegra_esp python3native perlnative kernel-artifact-names

IMAGE_TYPES += "tegraflash"

IMAGE_ROOTFS_ALIGNMENT ?= "4"

def tegra_default_rootfs_size(d):
    partsize = int(d.getVar('ROOTFSPART_SIZE')) // 1024
    extraspace = eval(d.getVar('IMAGE_ROOTFS_EXTRA_SPACE'))
    return str(partsize - extraspace)

IMAGE_ROOTFS_SIZE ?= "${@tegra_default_rootfs_size(d)}"

KERNEL_ARGS ??= ""
TEGRA_SIGNING_ARGS ??= ""
TEGRA_SIGNING_ENV ??= ""
TEGRA_SIGNING_EXCLUDE_TOOLS ??= ""
TEGRA_SIGNING_EXTRA_DEPS ??= ""
DTB_EXTRA_DEPS ??= "${@'virtual/dtb:do_populate_sysroot' if d.getVar('PREFERRED_PROVIDER_virtual/dtb') else ''}"
EXTERNAL_KERNEL_DEVICETREE ??= "${@'${RECIPE_SYSROOT}/boot/devicetree' if d.getVar('PREFERRED_PROVIDER_virtual/dtb') else ''}"

TEGRA_BUPGEN_SPECS ??= "boardid=${TEGRA_BOARDID};fab=${TEGRA_FAB};boardrev=${TEGRA_BOARDREV};chiprev=${TEGRA_CHIPREV}"
TEGRA_BUPGEN_STRIP_IMG_NAMES ??= ""
TEGRA_BUPGEN_STRIP_CMD ??= "${@tegraflash_bupgen_strip_cmd(d)}"

DTBFILE ?= "${@os.path.basename(d.getVar('KERNEL_DEVICETREE').split()[0])}"
LNXFILE ?= "boot.img"
LNXSIZE ?= "83886080"
RECROOTFSSIZE ?= "314572800"

IMAGE_TEGRAFLASH_FS_TYPE ??= "ext4"
IMAGE_TEGRAFLASH_ROOTFS ?= "${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.${IMAGE_TEGRAFLASH_FS_TYPE}"

def tegra_initrd_image(d):
    if d.getVar('IMAGE_UBOOT'):
        return ''
    if d.getVar('INITRAMFS_IMAGE'):
        return '' if bb.utils.to_boolean(d.getVar('INITRAMFS_IMAGE_BUNDLE')) else '${INITRAMFS_IMAGE}'
    return ''

INITRD_IMAGE ?= "${@tegra_initrd_image(d)}"

def tegra_kernel_image(d):
    if d.getVar('INITRAMFS_IMAGE'):
        if bb.utils.to_boolean(d.getVar('INITRAMFS_IMAGE_BUNDLE')):
            img = '${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}-${INITRAMFS_LINK_NAME}.cboot'
        else:
            img = '${DEPLOY_DIR_IMAGE}/${INITRD_IMAGE}-${MACHINE}.cboot'
        return img
    return '${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}-${MACHINE}.cboot'

IMAGE_TEGRAFLASH_KERNEL ?= "${@tegra_kernel_image(d)}"
TEGRA_ESP_IMAGE ?= "tegra-espimage"
IMAGE_TEGRAFLASH_ESPIMG ?= "${DEPLOY_DIR_IMAGE}/${TEGRA_ESP_IMAGE}-${MACHINE}.esp"
DATAFILE ??= ""
IMAGE_TEGRAFLASH_DATA ??= ""

TEGRA_SPIFLASH_BOOT ??= ""
TEGRA_ROOTFS_AND_KERNEL_ON_SDCARD ??=""

TOSIMGFILENAME = "tos-optee.img"
TOSIMGFILENAME:tegra194 = "tos-optee_t194.img"
TOSIMGFILENAME:tegra234 = "tos-optee_t234.img"

BUP_PAYLOAD_DIR = "payloads_t${@d.getVar('NVIDIA_CHIP')[2:]}x"
FLASHTOOLS_DIR = "tegra-flash"

TEGRAFLASH_PACKAGE_FORMAT ??= "tar"
TEGRAFLASH_PACKAGE_FORMATS = "tar zip"
TEGRAFLASH_SDCARD_SIZE ??= "16G"

python() {
    pt = d.getVar('TEGRAFLASH_PACKAGE_FORMAT')
    valid_types = d.getVar('TEGRAFLASH_PACKAGE_FORMATS').split()
    if pt not in valid_types:
        bb.fatal("TEGRAFLASH_PACKAGE_FORMAT %s not recognized, supported types are: %s" % (pt, ', '.join(valid_types)))
}

# Override this function if you need to add
# customization after the default files are
# copied/symlinked into the working directory
# and before processing begins.
tegraflash_custom_pre() {
    :
}

tegraflash_post_sign_pkg() {
    :
}

tegraflash_post_sign_pkg:tegra194() {
    mksparse -b ${TEGRA_BLBLOCKSIZE} --fillpattern=0 "${IMAGE_TEGRAFLASH_ROOTFS}" ${IMAGE_BASENAME}.img
    [ "${TEGRA_SPIFLASH_BOOT}" = "1" -o "${TEGRA_ROOTFS_AND_KERNEL_ON_SDCARD}" = "1" ] || rm -f ./${IMAGE_BASENAME}.${IMAGE_TEGRAFLASH_FS_TYPE}
    sed -i -e"s,APPFILE_b,${IMAGE_BASENAME}.img," -e"s,APPFILE,${IMAGE_BASENAME}.img," secureflash.xml
    if [ -n "${IMAGE_TEGRAFLASH_DATA}" -a -n "${DATAFILE}" -a "${TEGRA_ROOTFS_AND_KERNEL_ON_SDCARD}" != "1" ]; then
        mksparse -b ${TEGRA_BLBLOCKSIZE} --fillpattern=0 "${IMAGE_TEGRAFLASH_DATA}" ${DATAFILE}.img
        sed -i -e"s,DATAFILE,${DATAFILE}.img," secureflash.xml
    fi
}

tegraflash_post_sign_pkg:tegra234() {
    mksparse -b ${TEGRA_BLBLOCKSIZE} --fillpattern=0 "${IMAGE_TEGRAFLASH_ROOTFS}" ${IMAGE_BASENAME}.img
    [ "${TEGRA_SPIFLASH_BOOT}" = "1" -o "${TEGRA_ROOTFS_AND_KERNEL_ON_SDCARD}" = "1" ] || rm -f ./${IMAGE_BASENAME}.${IMAGE_TEGRAFLASH_FS_TYPE}
    sed -i -e"s,APPFILE_b,${IMAGE_BASENAME}.img," -e"s,APPFILE,${IMAGE_BASENAME}.img," secureflash.xml
    if [ -n "${IMAGE_TEGRAFLASH_DATA}" -a -n "${DATAFILE}" -a "${TEGRA_ROOTFS_AND_KERNEL_ON_SDCARD}" != "1" ]; then
        mksparse -b ${TEGRA_BLBLOCKSIZE} --fillpattern=0 "${IMAGE_TEGRAFLASH_DATA}" ${DATAFILE}.img
        sed -i -e"s,DATAFILE,${DATAFILE}.img," secureflash.xml
    fi
}

# Override these functions to run a custom code signing
# step, such as packaging the flash/BUP contents and
# sending them to a remote code signing server.
#
# -- Function for signing a full tegraflash package --
#
# By default, if TEGRA_SIGNING_ARGS is defined, this function
# will run doflash.sh --no-flash to sign the binaries, then
# replace the doflash.sh script with the flashcmd.txt script
# that tegraflash.py generates, which will run tegraflash.py
# with the necessary arguments to send the signed binaries
# to the device.
tegraflash_custom_sign_pkg() {
    if [ -n "${TEGRA_SIGNING_ARGS}" ]; then
        ${TEGRA_SIGNING_ENV} ./doflash.sh --no-flash ${TEGRA_SIGNING_ARGS}
        [ -e flashcmd.txt ] || bbfatal "No flashcmd.txt generated by signing step"
        rm -rf doflash.sh secureflash.sh __pycache__
        mv flashcmd.txt doflash.sh
        chmod +x doflash.sh
        tegraflash_post_sign_pkg
    fi
}

# -- Function for BUP signing/creation --
# Note that this is *always* run. If no key is provided, binaries
# will be signed with a null key.
tegraflash_custom_sign_bup() {
    ./doflash.sh ${TEGRA_SIGNING_ARGS}
}

# Override this function if you need to add
# customization after other processing is done
# but before the zip package is created.
tegraflash_custom_post() {
    :
}

tegraflash_finalize_pkg() {
    if [ "${TEGRAFLASH_PACKAGE_FORMAT}" = "zip" ]; then
        rm -f ${IMGDEPLOYDIR}/${IMAGE_NAME}.tegraflash.zip
        zip -r ${IMGDEPLOYDIR}/${IMAGE_NAME}.tegraflash.zip .
        ln -sf ${IMAGE_NAME}.tegraflash.zip ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.tegraflash.zip
    else
        rm -f ${IMGDEPLOYDIR}/${IMAGE_NAME}.tegraflash.tar.gz
        ${IMAGE_CMD_TAR} --sparse --numeric-owner --transform="s,^\./,," -cf- . | gzip -f -9 -n -c --rsyncable > ${IMGDEPLOYDIR}/${IMAGE_NAME}.tegraflash.tar.gz
        ln -sf ${IMAGE_NAME}.tegraflash.tar.gz ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.tegraflash.tar.gz
    fi
}

tegraflash_create_flash_config() {
    :
}

tegraflash_create_flash_config:tegra194() {
    local destdir="$1"
    local lnxfile="$2"

    sed -e"s,MB1FILE,mb1_b_t194_prod.bin,2" "${STAGING_DATADIR}/tegraflash/${PARTITION_LAYOUT_TEMPLATE}" | \
	sed \
        -e"s,LNXFILE_b,$lnxfile," \
        -e"s,LNXFILE,$lnxfile," -e"s,LNXSIZE,${LNXSIZE}," \
        -e"s,TEGRABOOT,nvtboot_t194.bin," \
        -e"s,MTSPREBOOT,preboot_c10_prod_cr.bin," \
        -e"s,MTS_MCE,mce_c10_prod_cr.bin," \
        -e"s,MTSPROPER,mts_c10_prod_cr.bin," \
        -e"s,MB1FILE,mb1_t194_prod.bin," \
        -e"s,BPFFILE,bpmp-2_t194.bin," \
        -e"s,TBCFILE,uefi_jetson.bin," \
        -e"s,CAMERAFW,camera-rtcpu-t194-rce.img," \
        -e"s,DRAMECCTYPE,dram_ecc," -e"s,DRAMECCFILE,dram-ecc-t194.bin," -e"s,DRAMECCNAME,dram-ecc-fw," \
        -e"s,SPEFILE,spe_t194.bin," \
        -e"s,WB0BOOT,warmboot_t194_prod.bin," \
        -e"s,TOSFILE,${TOSIMGFILENAME}," \
        -e"s,EKSFILE,eks.img," \
        -e"s,RECNAME,recovery," -e"s,RECSIZE,66060288," -e"s,RECDTB-NAME,recovery-dtb," -e"s,BOOTCTRLNAME,kernel-bootctrl," \
        -e"/RECFILE/d" -e"/RECDTB-FILE/d" -e"/BOOTCTRL-FILE/d" \
        -e"s,APPSIZE,${ROOTFSPART_SIZE}," \
        -e"s,RECROOTFSSIZE,${RECROOTFSSIZE}," \
        -e"s,APPUUID_b,," -e"s,APPUUID,," \
	-e"s,ESP_FILE,esp.img," -e"/VARSTORE_FILE/d" \
        > $destdir/flash.xml.in
}

tegraflash_create_flash_config:tegra234() {
    local destdir="$1"
    local lnxfile="$2"

    cat "${STAGING_DATADIR}/tegraflash/${PARTITION_LAYOUT_TEMPLATE}" | sed \
        -e"s,LNXFILE_b,$lnxfile," \
        -e"s,LNXFILE,$lnxfile," -e"s,LNXSIZE,${LNXSIZE}," \
        -e"s,MB1FILE,mb1_t234_prod.bin," \
        -e"s,CAMERAFW,camera-rtcpu-t234-rce.img," \
        -e"s,SPEFILE,spe_t234.bin," \
        -e"s,TOSFILE,${TOSIMGFILENAME}," \
        -e"s,EKSFILE,eks.img," \
        -e"s,RECNAME,recovery," -e"s,RECSIZE,66060288," -e"s,RECDTB-NAME,recovery-dtb," \
        -e"/RECFILE/d" -e"/RECDTB-FILE/d" -e"/BOOTCTRL-FILE/d" \
        -e"s,APPSIZE,${ROOTFSPART_SIZE}," \
        -e"s,RECROOTFSSIZE,${RECROOTFSSIZE}," \
        -e"s,PSCBL1FILE,psc_bl1_t234_prod.bin," \
        -e"s,TSECFW,," \
        -e"s,NVHOSTNVDEC,nvdec_t234_prod.fw," \
        -e"s,MB2BLFILE,mb2_t234.bin," \
        -e"s,XUSB_FW,xusb_t234_prod.bin," \
        -e"s,BPFFILE,${BPF_FILE}," \
        -e"s,BPFDTB_FILE,${BPFDTB_FILE}," \
        -e"s,PSCFW,pscfw_t234_prod.bin," \
        -e"s,MCE_IMAGE,mce_flash_o10_cr_prod.bin," \
        -e"s,WB0FILE,sc7_t234_prod.bin," \
        -e"s,PSCRF_IMAGE,psc_rf_t234_prod.bin," \
        -e"s,MB2RF_IMAGE,mb2rf_t234.bin," \
        -e"s,TBCDTB-FILE,uefi_jetson_with_dtb.bin," \
        -e"s,DCE,display-t234-dce.bin," \
        -e"s,APPUUID_b,," -e"s,APPUUID,," \
	-e"s,ESP_FILE,esp.img," -e"/VARSTORE_FILE/d" \
        > $destdir/flash.xml.in
}

BOOTFILES = ""

BOOTFILES:tegra194 = "\
    adsp-fw.bin \
    bpmp-2_t194.bin \
    camera-rtcpu-t194-rce.img \
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
    spe_t194.bin \
    warmboot_t194_prod.bin \
    xusb_sil_rel_fw \
"

BOOTFILES:tegra234 = "\
    adsp-fw.bin \
    applet_t234.bin \
    ${BPF_FILE} \
    camera-rtcpu-t234-rce.img \
    eks.img \
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
    gpio.h \
    readinfo_t234_min_prod.xml \
    camera-rtcpu-sce.img \
"

copy_dtbs() {
    local destination=$1
    local dtb dtbf
    for dtb in ${KERNEL_DEVICETREE}; do
        dtbf=`basename $dtb`
        if [ -e $destination/$dtbf ]; then
            bbnote "Overwriting $destination/$dtbf with KERNEL_DEVICETREE content"
            rm -f $destination/$dtbf
        fi
        bbnote "Copying KERNEL_DEVICETREE entry $dtbf to $destination"
        cp -L "${DEPLOY_DIR_IMAGE}/$dtbf" $destination/$dtbf
    done
    if [ -n "${EXTERNAL_KERNEL_DEVICETREE}" ]; then
        for dtb in $(find "${EXTERNAL_KERNEL_DEVICETREE}" \( -name '*.dtb' -o -name '*.dtbo' \) -printf '%P\n' | sort); do
            dtbf=`basename $dtb`
            if [ -e $destination/$dtbf ]; then
                bbnote "Overwriting $destination/$dtbf with EXTERNAL_KERNEL_DEVICETREE content"
                rm -f $destination/$dtbf
            fi
            bbnote "Copying EXTERNAL_KERNEL_DEVICETREE entry $dtbf to $destination"
            cp -L "${EXTERNAL_KERNEL_DEVICETREE}/$dtbf" $destination/$dtbf
        done
    fi
}

create_tegraflash_pkg() {
    :
}

create_tegraflash_pkg:tegra194() {
    local f
    PATH="${STAGING_BINDIR_NATIVE}/${FLASHTOOLS_DIR}:${PATH}"
    rm -rf "${WORKDIR}/tegraflash"
    mkdir -p "${WORKDIR}/tegraflash"
    oldwd=`pwd`
    cd "${WORKDIR}/tegraflash"
    cp "${STAGING_DATADIR}/tegraflash/bsp_version" .
    cp "${STAGING_DATADIR}/tegraflash/${EMMC_BCT}" .
    cp "${STAGING_DATADIR}/tegraflash/${EMMC_BCT_OVERRIDE}" .
    cp "${IMAGE_TEGRAFLASH_KERNEL}" ./${LNXFILE}
    cp "${IMAGE_TEGRAFLASH_ESPIMG}" ./esp.img
    if [ -n "${DATAFILE}" -a -n "${IMAGE_TEGRAFLASH_DATA}" ]; then
        cp "${IMAGE_TEGRAFLASH_DATA}" ./${DATAFILE}
        DATAARGS="--datafile ${DATAFILE}"
    fi
    cp "${DEPLOY_DIR_IMAGE}/uefi_jetson.bin" ./uefi_jetson.bin
    cp "${DEPLOY_DIR_IMAGE}/tos-${MACHINE}.img" ./${TOSIMGFILENAME}
    for f in ${BOOTFILES}; do
        cp "${STAGING_DATADIR}/tegraflash/$f" .
    done
    cp mb1_t194_prod.bin mb1_b_t194_prod.bin
    rm -rf ./rollback
    mkdir ./rollback
    cp -R ${STAGING_DATADIR}/nv_tegra/rollback/t${@d.getVar('NVIDIA_CHIP')[2:]}x ./rollback/
    cp ${STAGING_DATADIR}/tegraflash/flashvars .
    sed -i -e "s/@OVERLAY_DTB_FILE@/${OVERLAY_DTB_FILE}/" ./flashvars
    for f in ${STAGING_DATADIR}/tegraflash/tegra19[4x]-*.cfg; do
        cp $f .
    done
    for f in ${STAGING_DATADIR}/tegraflash/tegra194-*-bpmp-*.dtb; do
        cp $f .
    done
    cp ${DEPLOY_DIR_IMAGE}/*.dtbo .
    copy_dtbs "${WORKDIR}/tegraflash"
    if [ "${TEGRA_SIGNING_EXCLUDE_TOOLS}" != "1" ]; then
        cp -R ${STAGING_BINDIR_NATIVE}/${FLASHTOOLS_DIR}/* .
        mv ./rollback_parser.py ./rollback/
        tegraflash_generate_bupgen_script
    fi
    if [ -e ${STAGING_DATADIR}/tegraflash/odmfuse_pkc_${MACHINE}.xml ]; then
        cp ${STAGING_DATADIR}/tegraflash/odmfuse_pkc_${MACHINE}.xml ./odmfuse_pkc.xml
    fi
    tegraflash_custom_pre
    cp "${IMAGE_TEGRAFLASH_ROOTFS}" ./${IMAGE_BASENAME}.${IMAGE_TEGRAFLASH_FS_TYPE}
    tegraflash_create_flash_config "${WORKDIR}/tegraflash" ${LNXFILE}
    rm -f doflash.sh
    cat > doflash.sh <<END
#!/bin/sh
MACHINE=${TNSPEC_MACHINE} ./tegra194-flash-helper.sh $DATAARGS flash.xml.in ${DTBFILE} ${EMMC_BCT},${EMMC_BCT_OVERRIDE} ${ODMDATA} ${LNXFILE} ${IMAGE_BASENAME}.${IMAGE_TEGRAFLASH_FS_TYPE} "\$@"
END
    chmod +x doflash.sh
    if [ -e ./odmfuse_pkc.xml ]; then
        cat > burnfuses.sh <<END
#!/bin/sh
MACHINE=${TNSPEC_MACHINE} ./tegra194-flash-helper.sh -c "burnfuses odmfuse_pkc.xml" --no-flash $DATAARGS flash.xml.in ${DTBFILE} ${EMMC_BCT},${EMMC_BCT_OVERRIDE} ${ODMDATA} ${LNXFILE} ${IMAGE_BASENAME}.${IMAGE_TEGRAFLASH_FS_TYPE} "\$@"
END
        chmod +x burnfuses.sh
    fi
    if [ "${TEGRA_SPIFLASH_BOOT}" = "1" ]; then
        rm -f dosdcard.sh
        cat > dosdcard.sh <<END
#!/bin/sh
MACHINE=${TNSPEC_MACHINE} BOARDID=\${BOARDID:-${TEGRA_BOARDID}} FAB=\${FAB:-${TEGRA_FAB}} CHIPREV=\${CHIPREV:-${TEGRA_CHIPREV}} BOARDSKU=\${BOARDSKU:-${TEGRA_BOARDSKU}} ./tegra194-flash-helper.sh --sdcard -B ${TEGRA_BLBLOCKSIZE} -s ${TEGRAFLASH_SDCARD_SIZE} -b ${IMAGE_BASENAME} $DATAARGS flash.xml.in ${DTBFILE} ${EMMC_BCT},${EMMC_BCT_OVERRIDE} ${ODMDATA} ${LNXFILE} ${IMAGE_BASENAME}.${IMAGE_TEGRAFLASH_FS_TYPE} "\$@"
END
        chmod +x dosdcard.sh
    elif [ "${TEGRA_ROOTFS_AND_KERNEL_ON_SDCARD}" = "1" ]; then
        rm -f doflash.sh
        cat > doflash.sh <<END
#!/bin/sh
./nvflashxmlparse --split=flash-sdcard.xml.in --output=flash-mmc.xml.in --sdcard-size=${TEGRAFLASH_SDCARD_SIZE} flash.xml.in
MACHINE=${TNSPEC_MACHINE} ./tegra194-flash-helper.sh $DATAARGS flash-mmc.xml.in ${DTBFILE} ${EMMC_BCT},${EMMC_BCT_OVERRIDE} ${ODMDATA} ${LNXFILE} ${IMAGE_BASENAME}.${IMAGE_TEGRAFLASH_FS_TYPE} "\$@"
END
        chmod +x doflash.sh
        rm -f dosdcard.sh
        cat > dosdcard.sh <<END
#!/bin/sh
./nvflashxmlparse --split=flash-sdcard.xml.in --output=flash-mmc.xml.in --sdcard-size=${TEGRAFLASH_SDCARD_SIZE} flash.xml.in
MACHINE=${TNSPEC_MACHINE} BOARDID=\${BOARDID:-${TEGRA_BOARDID}} FAB=\${FAB:-${TEGRA_FAB}} CHIPREV=\${CHIPREV:-${TEGRA_CHIPREV}} BOARDSKU=\${BOARDSKU:-${TEGRA_BOARDSKU}} BOARDREV=\${BOARDREV:-${TEGRA_BOARDREV}} ./tegra194-flash-helper.sh --sdcard -B ${TEGRA_BLBLOCKSIZE} -s ${TEGRAFLASH_SDCARD_SIZE} -b ${IMAGE_BASENAME} $DATAARGS flash-sdcard.xml.in ${DTBFILE} ${EMMC_BCT},${EMMC_BCT_OVERRIDE} ${ODMDATA} ${LNXFILE} ${IMAGE_BASENAME}.${IMAGE_TEGRAFLASH_FS_TYPE} "\$@"
END
        chmod +x dosdcard.sh
    fi
    tegraflash_custom_post
    tegraflash_custom_sign_pkg
    tegraflash_finalize_pkg
    cd $oldwd
}

create_tegraflash_pkg:tegra234() {
    local f
    PATH="${STAGING_BINDIR_NATIVE}/${FLASHTOOLS_DIR}:${PATH}"
    rm -rf "${WORKDIR}/tegraflash"
    mkdir -p "${WORKDIR}/tegraflash"
    oldwd=`pwd`
    cd "${WORKDIR}/tegraflash"
    cp "${STAGING_DATADIR}/tegraflash/bsp_version" .
    cp "${STAGING_DATADIR}/tegraflash/${EMMC_BCT}" .
    cp "${IMAGE_TEGRAFLASH_KERNEL}" ./${LNXFILE}
    cp "${IMAGE_TEGRAFLASH_ESPIMG}" ./esp.img
    if [ -n "${DATAFILE}" -a -n "${IMAGE_TEGRAFLASH_DATA}" ]; then
        cp "${IMAGE_TEGRAFLASH_DATA}" ./${DATAFILE}
        DATAARGS="--datafile ${DATAFILE}"
    fi
    cp "${DEPLOY_DIR_IMAGE}/uefi_jetson.bin" ./uefi_jetson.bin
    cp "${DEPLOY_DIR_IMAGE}/tos-${MACHINE}.img" ./${TOSIMGFILENAME}
    for f in ${BOOTFILES}; do
        cp "${STAGING_DATADIR}/tegraflash/$f" .
    done

    # Copy and update flashvars
    cp ${STAGING_DATADIR}/tegraflash/flashvars .
    sed -i -e "s/@BPF_FILE@/${BPF_FILE}/" \
    -e "s/@BPFDTB_FILE@/${BPFDTB_FILE}/" \
    -e "s/@TBCDTB_FILE@/${TBCDTB_FILE}/" \
    -e "s/@WB0SDRAM_BCT@/${WB0SDRAM_BCT}/" \
    -e "s/@OVERLAY_DTB_FILE@/${OVERLAY_DTB_FILE}/" \
    ./flashvars

    for f in ${STAGING_DATADIR}/tegraflash/tegra234-*.dts*; do
        cp $f .
    done
    cp ${DEPLOY_DIR_IMAGE}/*.dtbo .
    for f in ${STAGING_DATADIR}/tegraflash/tegra234-bpmp-*.dtb; do
        cp $f .
    done
    copy_dtbs "${WORKDIR}/tegraflash"
    if [ "${TEGRA_SIGNING_EXCLUDE_TOOLS}" != "1" ]; then
        cp -R ${STAGING_BINDIR_NATIVE}/${FLASHTOOLS_DIR}/* .
        rm ./rollback_parser.py
        tegraflash_generate_bupgen_script
    fi

    if [ -e ${STAGING_DATADIR}/tegraflash/odmfuse_pkc_${MACHINE}.xml ]; then
        cp ${STAGING_DATADIR}/tegraflash/odmfuse_pkc_${MACHINE}.xml ./odmfuse_pkc.xml
    fi
    tegraflash_custom_pre
    cp "${IMAGE_TEGRAFLASH_ROOTFS}" ./${IMAGE_BASENAME}.${IMAGE_TEGRAFLASH_FS_TYPE}
    tegraflash_create_flash_config "${WORKDIR}/tegraflash" ${LNXFILE}
    rm -f doflash.sh
    cat > doflash.sh <<END
#!/bin/sh
MACHINE=${TNSPEC_MACHINE} BOARDID=\${BOARDID:-${TEGRA_BOARDID}} FAB=\${FAB:-${TEGRA_FAB}} CHIPREV=\${CHIPREV:-${TEGRA_CHIPREV}} BOARDSKU=\${BOARDSKU:-${TEGRA_BOARDSKU}} ./tegra234-flash-helper.sh $DATAARGS flash.xml.in ${DTBFILE} ${EMMC_BCT} ${ODMDATA} ${LNXFILE} ${IMAGE_BASENAME}.${IMAGE_TEGRAFLASH_FS_TYPE} "\$@"
END
    chmod +x doflash.sh

    if [ -e ./odmfuse_pkc.xml ]; then
        cat > burnfuses.sh <<END
#!/bin/sh
MACHINE=${TNSPEC_MACHINE} ./tegra234-flash-helper.sh -c "burnfuses odmfuse_pkc.xml" --no-flash $DATAARGS flash.xml.in ${DTBFILE} ${EMMC_BCT} ${ODMDATA} ${LNXFILE} ${IMAGE_BASENAME}.${IMAGE_TEGRAFLASH_FS_TYPE} "\$@"
END
        chmod +x burnfuses.sh
    fi

    if [ "${TEGRA_SPIFLASH_BOOT}" = "1" ]; then
        rm -f dosdcard.sh
        cat > dosdcard.sh <<END
#!/bin/sh
MACHINE=${TNSPEC_MACHINE} BOARDID=\${BOARDID:-${TEGRA_BOARDID}} FAB=\${FAB:-${TEGRA_FAB}} CHIPREV=\${CHIPREV:-${TEGRA_CHIPREV}} BOARDSKU=\${BOARDSKU:-${TEGRA_BOARDSKU}} ./tegra234-flash-helper.sh --sdcard -B ${TEGRA_BLBLOCKSIZE} -s ${TEGRAFLASH_SDCARD_SIZE} -b ${IMAGE_BASENAME} $DATAARGS flash.xml.in ${DTBFILE} ${EMMC_BCT} ${ODMDATA} ${LNXFILE} ${IMAGE_BASENAME}.${IMAGE_TEGRAFLASH_FS_TYPE} "\$@"
END
        chmod +x dosdcard.sh
    elif [ "${TEGRA_ROOTFS_AND_KERNEL_ON_SDCARD}" = "1" ]; then
        rm -f doflash.sh
        cat > doflash.sh <<END
#!/bin/sh
./nvflashxmlparse --split=flash-sdcard.xml.in --output=flash-mmc.xml.in --sdcard-size=${TEGRAFLASH_SDCARD_SIZE} flash.xml.in
MACHINE=${TNSPEC_MACHINE} ./tegra234-flash-helper.sh $DATAARGS flash-mmc.xml.in ${DTBFILE} ${EMMC_BCT} ${ODMDATA} ${LNXFILE} ${IMAGE_BASENAME}.${IMAGE_TEGRAFLASH_FS_TYPE} "\$@"
END
        chmod +x doflash.sh
        rm -f dosdcard.sh
        cat > dosdcard.sh <<END
#!/bin/sh
./nvflashxmlparse --split=flash-sdcard.xml.in --output=flash-mmc.xml.in --sdcard-size=${TEGRAFLASH_SDCARD_SIZE} flash.xml.in
MACHINE=${TNSPEC_MACHINE} BOARDID=\${BOARDID:-${TEGRA_BOARDID}} FAB=\${FAB:-${TEGRA_FAB}} CHIPREV=\${CHIPREV:-${TEGRA_CHIPREV}} BOARDSKU=\${BOARDSKU:-${TEGRA_BOARDSKU}} BOARDREV=\${BOARDREV:-${TEGRA_BOARDREV}} ./tegra234-flash-helper.sh --sdcard -B ${TEGRA_BLBLOCKSIZE} -s ${TEGRAFLASH_SDCARD_SIZE} -b ${IMAGE_BASENAME} $DATAARGS flash-sdcard.xml.in ${DTBFILE} ${EMMC_BCT} ${ODMDATA} ${LNXFILE} ${IMAGE_BASENAME}.${IMAGE_TEGRAFLASH_FS_TYPE} "\$@"
END
        chmod +x dosdcard.sh
    fi
    tegraflash_custom_post
    tegraflash_custom_sign_pkg
    tegraflash_finalize_pkg
    cd $oldwd
}

create_tegraflash_pkg[vardepsexclude] += "DATETIME"

def tegraflash_bupgen_strip_cmd(d):
    images = d.getVar('TEGRA_BUPGEN_STRIP_IMG_NAMES').split()
    if len(images) == 0:
        return 'cp flash.xml.in flash-stripped.xml.in'
    return 'sed {} flash.xml.in > flash-stripped.xml.in'.format(' '.join(['-e"/<filename>.*{}/d"'.format(img) for img in images]))


tegraflash_generate_bupgen_script() {
    local outfile="${1:-./generate_bup_payload.sh}"
    local spec__ sdramcfg fab boardsku boardrev
    rm -f $outfile
    cat <<EOF > $outfile
#!/bin/bash
${TEGRA_BUPGEN_STRIP_CMD}
rm -rf signed multi_signed rollback.bin ${BUP_PAYLOAD_DIR}
export BOARDID=${TEGRA_BOARDID}
export fuselevel=fuselevel_production
export localbootfile=${LNXFILE}
export CHIPREV=${TEGRA_CHIPREV}
EOF
    if [ "${SOC_FAMILY}" = "tegra194" ]; then
        sdramcfg="${EMMC_BCT},${EMMC_BCT_OVERRIDE}"
    else
        sdramcfg="${EMMC_BCT}"
    fi
    fab="${TEGRA_FAB}"
    boardsku="${TEGRA_BOARDSKU}"
    boardrev="${TEGRA_BOARDREV}"
    for spec__ in ${@' '.join(['"%s"' % entry for entry in d.getVar('TEGRA_BUPGEN_SPECS').split()])}; do
        eval $spec__
        cat <<EOF >> $outfile
MACHINE=${TNSPEC_MACHINE} FAB="$fab" BOARDSKU="$boardsku" BOARDREV="$boardrev" ./${SOC_FAMILY}-flash-helper.sh --bup ./flash-stripped.xml.in ${DTBFILE} $sdramcfg ${ODMDATA} "\$@"
EOF
    done
    chmod +x $outfile
}

IMAGE_CMD:tegraflash = "create_tegraflash_pkg"
TEGRAFLASH_PKG_DEPENDS = "${@'zip-native:do_populate_sysroot' if d.getVar('TEGRAFLASH_PACKAGE_FORMAT') == 'zip' else '${CONVERSION_DEPENDS_gz}:do_populate_sysroot'}"
do_image_tegraflash[depends] += "${TEGRAFLASH_PKG_DEPENDS} dtc-native:do_populate_sysroot coreutils-native:do_populate_sysroot \
                                 tegra-flashtools-native:do_populate_sysroot gptfdisk-native:do_populate_sysroot \
                                 tegra-bootfiles:do_populate_sysroot tegra-bootfiles:do_populate_lic \
                                 tegra-redundant-boot-rollback:do_populate_sysroot virtual/kernel:do_deploy \
                                 ${@'${INITRD_IMAGE}:do_image_complete' if d.getVar('INITRD_IMAGE') != '' else  ''} \
                                 virtual/bootloader:do_deploy virtual/secure-os:do_deploy ${TEGRA_ESP_IMAGE}:do_image_complete ${TEGRA_SIGNING_EXTRA_DEPS} ${DTB_EXTRA_DEPS}"
IMAGE_TYPEDEP:tegraflash += "${IMAGE_TEGRAFLASH_FS_TYPE}"

oe_make_bup_payload() {
    PATH="${STAGING_BINDIR_NATIVE}/${FLASHTOOLS_DIR}:${PATH}"
    export tosimgfilename=${TOSIMGFILENAME}
    rm -rf ${WORKDIR}/bup-payload
    mkdir ${WORKDIR}/bup-payload
    oldwd="$PWD"
    cd ${WORKDIR}/bup-payload
    # BUP generator really wants to use 'boot.img' for the LNX
    # partition contents
    cp $1 ./boot.img
    tegraflash_create_flash_config "${WORKDIR}/bup-payload" boot.img
    cp "${STAGING_DATADIR}/tegraflash/bsp_version" .
    cp "${STAGING_DATADIR}/tegraflash/${EMMC_BCT}" .
    if [ "${SOC_FAMILY}" = "tegra194" ]; then
        cp "${STAGING_DATADIR}/tegraflash/${EMMC_BCT_OVERRIDE}" .
    fi
    cp "${DEPLOY_DIR_IMAGE}/uefi_jetson.bin" ./
    cp "${DEPLOY_DIR_IMAGE}/tos-${MACHINE}.img" ./$tosimgfilename
    cp "${IMAGE_TEGRAFLASH_ESPIMG}" ./esp.img
    for f in ${BOOTFILES}; do
        cp "${STAGING_DATADIR}/tegraflash/$f" .
    done
    cp ${STAGING_DATADIR}/tegraflash/flashvars .
    cp ${DEPLOY_DIR_IMAGE}/*.dtbo .
    if [ "${SOC_FAMILY}" = "tegra194" ]; then
        cp mb1_t194_prod.bin mb1_b_t194_prod.bin
        sed -i -e "s/@OVERLAY_DTB_FILE@/${OVERLAY_DTB_FILE}/" ./flashvars
        for f in ${STAGING_DATADIR}/tegraflash/tegra19[4x]-*.cfg; do
            cp $f .
        done
        for f in ${STAGING_DATADIR}/tegraflash/tegra194-*-bpmp-*.dtb; do
            cp $f .
        done
    elif [ "${SOC_FAMILY}" = "tegra234" ]; then
        sed -i -e "s/@BPF_FILE@/${BPF_FILE}/" \
	    -e "s/@BPFDTB_FILE@/${BPFDTB_FILE}/" \
	    -e "s/@TBCDTB_FILE@/${TBCDTB_FILE}/" \
	    -e "s/@WB0SDRAM_BCT@/${WB0SDRAM_BCT}/" \
	    -e "s/@OVERLAY_DTB_FILE@/${OVERLAY_DTB_FILE}/" \
	    ./flashvars
        for f in ${STAGING_DATADIR}/tegraflash/tegra234-*.dts*; do
            cp $f .
        done
        for f in ${STAGING_DATADIR}/tegraflash/tegra234-bpmp-*.dtb; do
            cp $f .
        done
    fi
    . ./flashvars
    copy_dtbs "${WORKDIR}/bup-payload"
    if [ -n "${NVIDIA_BOARD_CFG}" ]; then
        cp "${STAGING_DATADIR}/tegraflash/board_config_${MACHINE}.xml" .
        boardcfg=board_config_${MACHINE}.xml
    else
        boardcfg=
    fi
    export boardcfg
    mkdir ./rollback
    if [ "${SOC_FAMILY}" = "tegra194" ]; then
        cp -R ${STAGING_DATADIR}/nv_tegra/rollback/t${@d.getVar('NVIDIA_CHIP')[2:]}x ./rollback/
    fi
    if [ "${TEGRA_SIGNING_EXCLUDE_TOOLS}" != "1" ]; then
        cp -R ${STAGING_BINDIR_NATIVE}/${FLASHTOOLS_DIR}/* ./
	mv ./rollback_parser.py ./rollback/
        sed -i -e 's,^function ,,' ./l4t_bup_gen.func
        tegraflash_generate_bupgen_script ./doflash.sh
    fi
    tegraflash_custom_sign_bup
    for bup in ${WORKDIR}/bup-payload/${BUP_PAYLOAD_DIR}/*; do
	[ -e $bup ] || continue
	BUP_generator.py --contents --check $bup
    done
    mv ${WORKDIR}/bup-payload/${BUP_PAYLOAD_DIR}/* .
    cd "$oldwd"
}

create_bup_payload_image() {
    local type="$1"
    oe_make_bup_payload ${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}
    install -m 0644 ${WORKDIR}/bup-payload/bl_update_payload ${IMGDEPLOYDIR}/${IMAGE_NAME}.bup-payload
    ln -sf ${IMAGE_NAME}.bup-payload ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.bup-payload
    for f in ${WORKDIR}/bup-payload/*_only_payload; do
        [ -e $f ] || continue
        sfx=$(basename $f _payload)
        install -m 0644 $f ${IMGDEPLOYDIR}/${IMAGE_NAME}.$sfx.bup-payload
        ln -sf ${IMAGE_NAME}.$sfx.bup-payload ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.$sfx.bup-payload
    done
}
create_bup_payload_image[vardepsexclude] += "DATETIME"

CONVERSIONTYPES += "bup-payload"
CONVERSION_DEPENDS_bup-payload = "tegra-flashtools-native python3-pyyaml-native coreutils-native tegra-bootfiles tegra-redundant-boot-rollback dtc-native virtual/bootloader:do_deploy virtual/kernel:do_deploy virtual/secure-os:do_deploy ${TEGRA_ESP_IMAGE}:do_image_complete ${TEGRA_SIGNING_EXTRA_DEPS} ${DTB_EXTRA_DEPS}"
CONVERSION_CMD:bup-payload = "create_bup_payload_image ${type}"
