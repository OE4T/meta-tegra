inherit image_types image_types_cboot python3native perlnative kernel-artifact-names

IMAGE_TYPES += "tegraflash ${TEGRA_BOOTPART_TYPE}"

IMAGE_ROOTFS_ALIGNMENT ?= "4"

def tegra_default_rootfs_size(d):
    partsize = int(d.getVar('ROOTFSPART_SIZE')) // 1024
    extraspace = eval(d.getVar('IMAGE_ROOTFS_EXTRA_SPACE'))
    return str(partsize - extraspace)

def tegra_rootfs_device(d):
    import re
    bootdev = d.getVar('TNSPEC_BOOTDEV')
    if bootdev.startswith("mmc") or bootdev.startswith("nvme"):
        return re.sub(r"p[0-9]+$", "", bootdev)
    return re.sub("[0-9]+$", "", bootdev)

IMAGE_ROOTFS_SIZE ?= "${@tegra_default_rootfs_size(d)}"

IMAGE_UBOOT ??= "u-boot"
KERNEL_ARGS ??= ""
TEGRA_SIGNING_ARGS ??= ""
TEGRA_SIGNING_ENV ??= ""
TEGRA_SIGNING_EXCLUDE_TOOLS ??= ""
TEGRA_SIGNING_EXTRA_DEPS ??= ""

TEGRA_BUPGEN_SPECS ??= "boardid=${TEGRA_BOARDID};fab=${TEGRA_FAB};boardrev=${TEGRA_BOARDREV};chiprev=${TEGRA_CHIPREV}"
TEGRA_BUPGEN_STRIP_IMG_NAMES ??= ""
TEGRA_BUPGEN_STRIP_CMD ??= "${@tegraflash_bupgen_strip_cmd(d)}"

DTBFILE ?= "${@os.path.basename(d.getVar('KERNEL_DEVICETREE').split()[0])}"
LNXFILE ?= "boot.img"
LNXSIZE ?= "83886080"
RECROOTFSSIZE ?= "314572800"

IMAGE_TEGRAFLASH_FS_TYPE ??= "ext4"
IMAGE_TEGRAFLASH_ROOTFS ?= "${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.${IMAGE_TEGRAFLASH_FS_TYPE}"
IMAGE_TEGRAFLASH_BOOTPART ?= "${@'${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.bootpart.${IMAGE_TEGRAFLASH_FS_TYPE}' if d.getVar('TEGRA_BOOTPART_TYPE') else ''}"
TEGRAFLASH_ROOTFS_EXTERNAL = "${@'1' if d.getVar('TNSPEC_BOOTDEV') != 'mmcblk0p1' else '0'}"
ROOTFS_DEVICE_FOR_INITRD_FLASH = "${@tegra_rootfs_device(d)}"
TEGRAFLASH_ERASE_MMC ?= "${TEGRAFLASH_ROOTFS_EXTERNAL}"

# Need a copy of /boot to install in the eMMC only on TX2 devices with U-Boot
def tegra_bootpart_type(d):
    if d.getVar("SOC_FAMILY") != "tegra186" or not d.getVar("IMAGE_UBOOT") or not bb.utils.to_boolean(d.getVar("TEGRAFLASH_ROOTFS_EXTERNAL")):
        return ""
    return "tegrabootpart"

TEGRA_BOOTPART_TYPE = "${@tegra_bootpart_type(d)}"
TEGRA_BOOTPART_SIZE ?= "262144"
BOOTPARTFILE ?= "${@'${IMAGE_LINK_NAME}.bootpart.${IMAGE_TEGRAFLASH_FS_TYPE}' if d.getVar('TEGRA_BOOTPART_TYPE') else ''}"

def tegra_initrd_image(d):
    if d.getVar('IMAGE_UBOOT'):
        return ''
    if d.getVar('INITRAMFS_IMAGE'):
        return '' if bb.utils.to_boolean(d.getVar('INITRAMFS_IMAGE_BUNDLE')) else '${INITRAMFS_IMAGE}'
    return ''

INITRD_IMAGE ?= "${@tegra_initrd_image(d)}"

def tegra_kernel_image(d):
    if d.getVar('IMAGE_UBOOT'):
        return '${DEPLOY_DIR_IMAGE}/${IMAGE_UBOOT}-${MACHINE}.bin'
    if d.getVar('INITRAMFS_IMAGE'):
        if bb.utils.to_boolean(d.getVar('INITRAMFS_IMAGE_BUNDLE')):
            img = '${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}-${INITRAMFS_LINK_NAME}.cboot'
        else:
            img = '${DEPLOY_DIR_IMAGE}/${INITRD_IMAGE}-${MACHINE}.cboot'
        return img
    return '${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}-${MACHINE}.cboot'

IMAGE_TEGRAFLASH_KERNEL ?= "${@tegra_kernel_image(d)}"
DATAFILE ??= ""
IMAGE_TEGRAFLASH_DATA ??= ""

BL_IS_CBOOT = "${@'1' if d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('cboot') else '0'}"

def tegraflash_initrd_flasher(d):
    initramfs_image = d.getVar('TEGRAFLASH_INITRD_FLASH_IMAGE')
    if not initramfs_image:
        return ''
    if bb.utils.to_boolean(d.getVar('INITRAMFS_IMAGE_BUNDLE')):
        return '${DEPLOY_DIR_IMAGE}/initrd-flash-kernel/Image-initrd-flash.cboot'
    else:
        return '${DEPLOY_DIR_IMAGE}/%s-${MACHINE}.cboot' % initramfs_image

def tegraflash_initrd_flasher_deps(d):
    initramfs_image = d.getVar('TEGRAFLASH_INITRD_FLASH_IMAGE')
    if not initramfs_image:
        return ''
    if bb.utils.to_boolean(d.getVar('INITRAMFS_IMAGE_BUNDLE')):
        return 'linux-tegra-initrd-flash:do_deploy'
    else:
        return '{}:do_image_complete'.format(initramfs_image)

IMAGE_TEGRAFLASH_INITRD_FLASHER ?= "${@tegraflash_initrd_flasher(d)}"

TEGRA_SPIFLASH_BOOT ??= ""
TEGRA_ROOTFS_AND_KERNEL_ON_SDCARD ??=""

CBOOTFILENAME = "cboot.bin"
CBOOTFILENAME:tegra194 = "cboot_t194.bin"
TOSIMGFILENAME = "tos-trusty.img"
TOSIMGFILENAME:tegra194 = "tos-trusty_t194.img"
TOSIMGFILENAME:tegra210 = "tos-mon-only.img"
BMPBLOBFILENAME = "bmp.blob"

NVTBOOTFILENAME = "${@'nvtboot_rb.bin' if d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('cboot') else 'nvtboot.bin'}"
TBCFILENAME = "${@'nvtboot_cpu_rb.bin' if d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('cboot') else 'nvtboot_cpu.bin'}"
RCM_TBCFILENAME = "nvtboot_recovery_cpu.bin"

BUP_PAYLOAD_DIR = "payloads_t${@d.getVar('NVIDIA_CHIP')[2:]}x"
FLASHTOOLS_DIR = "${SOC_FAMILY}-flash"
FLASHTOOLS_DIR:tegra194 = "tegra186-flash"

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
    mksparse -b ${TEGRA_BLBLOCKSIZE} --fillpattern=0 "${IMAGE_TEGRAFLASH_ROOTFS}" ${IMAGE_BASENAME}.img
    cp secureflash.xml initrd-secureflash.xml
    sed -i -e"s,APPFILE,${IMAGE_BASENAME}.img," secureflash.xml
    if [ -n "${IMAGE_TEGRAFLASH_DATA}" -a -n "${DATAFILE}" ]; then
        mksparse -b ${TEGRA_BLBLOCKSIZE} --fillpattern=0 "${IMAGE_TEGRAFLASH_DATA}" ${DATAFILE}.img
        sed -i -e"s,DATAFILE,${DATAFILE}.img," secureflash.xml
    fi
    cat > .presigning-vars <<EOF
${@'\n'.join(d.getVar("TEGRA_SIGNING_ENV").split())}
EOF
}

tegraflash_sign_initrd_flash_kernel() {
    :
}

tegraflash_sign_initrd_flash_kernel:tegra186() {
    if [ -n "${TEGRA_SIGNING_ARGS}" -a -n "${IMAGE_TEGRAFLASH_INITRD_FLASHER}" ]; then
        tegra-signimage-helper --chip 0x18 --nosplit --type kernel initrd-flash.img ${TEGRA_SIGNING_ARGS}
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
        if [ -e external-flash.xml.in ]; then
	    cp doflash.sh doflash-external.sh
	    nvflashxmlparse --extract --type rootfs --change-device-type=sdmmc_user -o external-flash.xml.tmp external-flash.xml.in
	    sed -i -e's,flash\.xml\.in,external-flash.xml.tmp,' doflash-external.sh
	    ${TEGRA_SIGNING_ENV} ./doflash.sh --no-flash ${TEGRA_SIGNING_ARGS}
	    rm -f external-flash.xml.tmp flashcmd.txt doflash-external.sh
	fi
        ${TEGRA_SIGNING_ENV} ./doflash.sh --no-flash ${TEGRA_SIGNING_ARGS}
        [ -e flashcmd.txt ] || bbfatal "No flashcmd.txt generated by signing step"
	tegraflash_sign_initrd_flash_kernel
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

tegraflash_create_flash_config:tegra210() {
    local destdir="$1"
    local lnxfile="$2"
    local infile="$3"

    [ -n "$infile" ] || infile="${STAGING_DATADIR}/tegraflash/${PARTITION_LAYOUT_TEMPLATE}"
    sed \
        -e"s,EBTFILE,${CBOOTFILENAME}," \
        -e"s,LNXFILE,$lnxfile," \
        -e"/NCTFILE/d" -e"s,NCTTYPE,data," \
        -e"/SOSFILE/d" \
        -e"s,NXC,NVC," -e"s,NVCTYPE,bootloader," -e"s,NVCFILE,${NVTBOOTFILENAME}," \
        -e"s,MPBTYPE,data," -e"/MPBFILE/d" \
        -e"s,MBPTYPE,data," -e"/MBPFILE/d" \
        -e"s,BXF,BPF," -e"s,BPFFILE,sc7entry-firmware.bin," \
        -e"/BPFDTB-FILE/d" \
        -e"s,WX0,WB0," -e"s,WB0TYPE,WB0," -e"s,WB0FILE,warmboot.bin," \
        -e"s,TXS,TOS," -e"s,TOSFILE,${TOSIMGFILENAME}," \
        -e"s,EXS,EKS," -e"s,EKSFILE,eks.img," \
        -e"s,FBTYPE,data," -e"/FBFILE/d" \
        -e"s,DXB,DTB," \
        -e"s,APPSIZE,${ROOTFSPART_SIZE}," \
        -e"s,TXC,TBC," -e"s,TBCTYPE,bootloader," \
        -e"s,EFISIZE,67108864," -e"/EFIFILE/d" \
        -e"s,RECNAME,recovery," -e"s,RECSIZE,66060288," -e"s,RECDTB-NAME,recovery-dtb," -e"s,BOOTCTRLNAME,kernel-bootctrl," \
        -e"s,PPTSIZE,16896," \
        -e"s,RECROOTFSSIZE,${RECROOTFSSIZE}," \
        -e"s,APPUUID,," \
	"$infile" \
        > $destdir/flash.xml.in
}

tegraflash_create_flash_config:tegra186() {
    local destdir="$1"
    local lnxfile="$2"
    local infile="$3"

    [ -n "$infile" ] || infile="${STAGING_DATADIR}/tegraflash/${PARTITION_LAYOUT_TEMPLATE}"

    sed \
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
        -e"s,TBCFILE,${CBOOTFILENAME}," -e"s,TBCTYPE,bootloader," -e"s,TBCNAME,cpu-bootloader," \
        -e"s,TBCDTB-NAME,bootloader-dtb," \
        -e"s,SCEFILE,camera-rtcpu-sce.img," -e"s,SCENAME,sce-fw," -e"s,SCESIGN,true," \
        -e"s,SPEFILE,spe.bin," -e"s,SPENAME,spe-fw," -e"s,SPETYPE,spe_fw," \
        -e"s,WB0TYPE,WB0," -e"s,WB0FILE,warmboot.bin," -e"s,SC7NAME,sc7," \
        -e"s,TOSFILE,${TOSIMGFILENAME}," -e"s,TOSNAME,secure-os," \
        -e"s,EKSFILE,eks.img," \
        -e"s,FBNAME,fusebypass," -e"s,FBTYPE,data," -e"s,FBSIGN,false," -e"/FBFILE/d" \
        -e"s,KERNELDTB-NAME,kernel-dtb," \
        -e"s,APPSIZE,${ROOTFSPART_SIZE}," \
        -e"s,RECNAME,recovery," -e"s,RECSIZE,66060288," -e"s,RECDTB-NAME,recovery-dtb," -e"s,BOOTCTRLNAME,kernel-bootctrl," \
        -e"/RECFILE/d" -e"/RECDTB-FILE/d" -e"/BOOTCTRL-FILE/d" \
        -e"s,PPTSIZE,2097152," \
        -e"s,RECROOTFSSIZE,${RECROOTFSSIZE}," \
        -e"s,SMDFILE,${SMDFILE}," \
        -e"s,APPUUID,," \
	"$infile" \
        > $destdir/flash.xml.in
}

tegraflash_create_flash_config:tegra194() {
    local destdir="$1"
    local lnxfile="$2"
    local infile="$3"
    local cbotag

    [ -n "$infile" ] || infile="${STAGING_DATADIR}/tegraflash/${PARTITION_LAYOUT_TEMPLATE}"

    if [ -e cbo.dtb ]; then
        cbotag="-es,CBOOTOPTION_FILE,cbo.dtb,"
    else
        cbotag="-e/CBOOTOPTION_FILE/d"
    fi
    sed \
        -e"s,LNXFILE,$lnxfile," -e"s,LNXSIZE,${LNXSIZE}," \
        -e"s,TEGRABOOT,nvtboot_t194.bin," \
        -e"s,MTSPREBOOT,preboot_c10_prod_cr.bin," \
        -e"s,MTS_MCE,mce_c10_prod_cr.bin," \
        -e"s,MTSPROPER,mts_c10_prod_cr.bin," \
        -e"s,MB1FILE,mb1_t194_prod.bin," \
        -e"s,BPFFILE,bpmp_t194.bin," \
        -e"s,TBCFILE,${CBOOTFILENAME}," \
        -e"s,CAMERAFW,camera-rtcpu-rce.img," \
        -e"s,DRAMECCTYPE,dram_ecc," -e"s,DRAMECCFILE,dram-ecc-t194.bin," -e"s,DRAMECCNAME,dram-ecc-fw," \
        -e"s,SPEFILE,spe_t194.bin," \
        -e"s,WB0BOOT,warmboot_t194_prod.bin," \
        -e"s,TOSFILE,${TOSIMGFILENAME}," \
        -e"s,EKSFILE,eks.img," \
        $cbotag \
        -e"s,RECNAME,recovery," -e"s,RECSIZE,66060288," -e"s,RECDTB-NAME,recovery-dtb," -e"s,BOOTCTRLNAME,kernel-bootctrl," \
        -e"/RECFILE/d" -e"/RECDTB-FILE/d" -e"/BOOTCTRL-FILE/d" \
        -e"s,APPSIZE,${ROOTFSPART_SIZE}," \
        -e"s,RECROOTFSSIZE,${RECROOTFSSIZE}," \
        -e"s,SMDFILE,${SMDFILE}," \
        -e"s,APPUUID,," \
	"$infile" \
        > $destdir/flash.xml.in
}

BOOTFILES = ""
BOOTFILES:tegra210 = "\
    cboot_rb.bin \
    eks.img \
    nvtboot_recovery.bin \
    nvtboot_recovery_cpu.bin \
    nvtboot.bin \
    nvtboot_cpu.bin \
    nvtboot_cpu_rb.bin \
    nvtboot_rb.bin \
    warmboot.bin \
    rp4.blob \
    sc7entry-firmware.bin \
"
BOOTFILES:tegra186 = "\
    adsp-fw.bin \
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
    nvtboot.bin \
    warmboot.bin \
    minimal_scr.cfg \
    mobile_scr.cfg \
    emmc.cfg \
"

BOOTFILES:tegra194 = "\
    adsp-fw.bin \
    bpmp_t194.bin \
    camera-rtcpu-rce.img \
    dram-ecc-t194.bin \
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
    warmboot_t194_prod.bin \
    xusb_sil_rel_fw \
"

create_tegraflash_pkg() {
    :
}

create_tegraflash_pkg:tegra210() {
    PATH="${STAGING_BINDIR_NATIVE}/tegra210-flash:${PATH}"
    rm -rf "${WORKDIR}/tegraflash"
    mkdir -p "${WORKDIR}/tegraflash"
    oldwd=`pwd`
    cd "${WORKDIR}/tegraflash"
    cp "${STAGING_DATADIR}/tegraflash/bsp_version" .
    cp "${STAGING_DATADIR}/tegraflash/${MACHINE}.cfg" .
    cp "${IMAGE_TEGRAFLASH_KERNEL}" ./${LNXFILE}
    if [ -n "${IMAGE_TEGRAFLASH_INITRD_FLASHER}" ]; then
        cp "${IMAGE_TEGRAFLASH_INITRD_FLASHER}" ./initrd-flash.img
    fi
    if [ -n "${DATAFILE}" -a -n "${IMAGE_TEGRAFLASH_DATA}" ]; then
        cp "${IMAGE_TEGRAFLASH_DATA}" ./${DATAFILE}
        DATAARGS="--datafile ${DATAFILE}"
    fi
    cp ${STAGING_DATADIR}/tegraflash/flashvars .
    echo "TBCFILENAME=${TBCFILENAME}" >> flashvars
    echo "RCM_TBCFILENAME=${RCM_TBCFILENAME}" >> flashvars
    for f in ${KERNEL_DEVICETREE}; do
        dtbf=`basename $f`
        cp -L "${DEPLOY_DIR_IMAGE}/$dtbf" ./
        if [ -n "${KERNEL_ARGS}" ]; then
            fdtput -t s ./$dtbf /chosen bootargs "${KERNEL_ARGS}"
        elif fdtget -t s ./$dtbf /chosen bootargs >/dev/null 2>&1; then
            fdtput -d ./$dtbf /chosen bootargs
        fi
    done
    cp "${DEPLOY_DIR_IMAGE}/cboot-${MACHINE}.bin" ./${CBOOTFILENAME}
    cp "${DEPLOY_DIR_IMAGE}/tos-${MACHINE}.img" ./${TOSIMGFILENAME}
    cp "${DEPLOY_DIR_IMAGE}/bootlogo-${MACHINE}.blob" ./${BMPBLOBFILENAME}
    for f in ${BOOTFILES}; do
        cp "${STAGING_DATADIR}/tegraflash/$f" .
    done
    if [ -n "${NVIDIA_BOARD_CFG}" ]; then
        cp "${STAGING_DATADIR}/tegraflash/board_config_${MACHINE}.xml" .
        echo "boardcfg_file=board_config_${MACHINE}.xml" >> flashvars
    fi

    if [ "${TEGRA_SIGNING_EXCLUDE_TOOLS}" != "1" ]; then
        cp -R ${STAGING_BINDIR_NATIVE}/tegra210-flash/* .
        if [ -z "${IMAGE_TEGRAFLASH_INITRD_FLASHER}" ]; then
            rm -f ./initrd-flash
        fi
        tegraflash_generate_bupgen_script
    fi
    if [ -e ${STAGING_DATADIR}/tegraflash/odmfuse_pkc_${MACHINE}.xml ]; then
        cp ${STAGING_DATADIR}/tegraflash/odmfuse_pkc_${MACHINE}.xml ./odmfuse_pkc.xml
    fi
    tegraflash_custom_pre
    cp "${IMAGE_TEGRAFLASH_ROOTFS}" ./${IMAGE_BASENAME}.${IMAGE_TEGRAFLASH_FS_TYPE}
    tegraflash_create_flash_config "${WORKDIR}/tegraflash" ${LNXFILE}
    if [ "${TEGRAFLASH_ROOTFS_EXTERNAL}" = "1" ]; then
        rm -rf "${WORKDIR}/tegraflash/external" external-flash.xml.in
	mkdir "${WORKDIR}/tegraflash/external"
        tegraflash_create_flash_config "${WORKDIR}/tegraflash/external" ${LNXFILE} ${STAGING_DATADIR}/tegraflash/${PARTITION_LAYOUT_EXTERNAL}
	mv external/flash.xml.in ./external-flash.xml.in
	rm -rf external
    fi

    rm -f doflash.sh
    cat > doflash.sh <<END
#!/bin/sh
MACHINE=${MACHINE} ./tegra210-flash-helper.sh -B ${TEGRA_BLBLOCKSIZE} $DATAARGS flash.xml.in ${DTBFILE} ${MACHINE}.cfg ${ODMDATA} ${LNXFILE} ${IMAGE_BASENAME}.${IMAGE_TEGRAFLASH_FS_TYPE} "\$@"
END
    chmod +x doflash.sh
    if [ -n "${IMAGE_TEGRAFLASH_INITRD_FLASHER}" ]; then
        rm -f .env.initrd-flash
        cat > .env.initrd-flash <<END
FLASH_HELPER=${SOC_FAMILY}-flash-helper.sh
BOOTDEV="${TNSPEC_BOOTDEV}"
ROOTFS_DEVICE="${ROOTFS_DEVICE_FOR_INITRD_FLASH}"
MACHINE="${MACHINE}"
CHIPID="${NVIDIA_CHIP}"
DEFAULTS[BOARDID]="${TEGRA_BOARDID}"
DEFAULTS[FAB]="${TEGRA_FAB}"
DEFAULTS[fuselevel]="fuselevel_production"
DTBFILE="${DTBFILE}"
EMMC_BCTS="${MACHINE}.cfg"
ODMDATA="${ODMDATA}"
LNXFILE="${LNXFILE}"
ROOTFS_IMAGE="${IMAGE_BASENAME}.${IMAGE_TEGRAFLASH_FS_TYPE}"
DATAFILE="${DATAFILE}"
BOOTPARTFILE="${BOOTPARTFILE}"
EXTERNAL_ROOTFS_DRIVE=${TEGRAFLASH_ROOTFS_EXTERNAL}
BOOT_PARTITIONS_ON_EMMC=${BOOT_PARTITIONS_ON_EMMC}
END
    fi
    if [ -e ./odmfuse_pkc.xml ]; then
        cat > burnfuses.sh <<END
#!/bin/sh
MACHINE=${MACHINE} ./tegra210-flash-helper.sh -B ${TEGRA_BLBLOCKSIZE} -c "blowfuses odmfuse_pkc.xml" --no-flash $DATAARGS flash.xml.in ${DTBFILE} ${MACHINE}.cfg ${ODMDATA} ${LNXFILE} ${IMAGE_BASENAME}.${IMAGE_TEGRAFLASH_FS_TYPE} "\$@"
END
        chmod +x burnfuses.sh
    fi
    if [ "${TEGRA_SPIFLASH_BOOT}" = "1" ]; then
        rm -f dosdcard.sh
        cat > dosdcard.sh <<END
#!/bin/sh
MACHINE=${MACHINE} BOARDID=\${BOARDID:-${TEGRA_BOARDID}} FAB=\${FAB:-${TEGRA_FAB}} ./tegra210-flash-helper.sh --sdcard -B ${TEGRA_BLBLOCKSIZE} -s ${TEGRAFLASH_SDCARD_SIZE} -b ${IMAGE_BASENAME} $DATAARGS flash.xml.in ${DTBFILE} ${MACHINE}.cfg ${ODMDATA} ${LNXFILE} ${IMAGE_BASENAME}.${IMAGE_TEGRAFLASH_FS_TYPE} "\$@"
END
        chmod +x dosdcard.sh
    fi
    tegraflash_custom_post
    tegraflash_custom_sign_pkg
    tegraflash_finalize_pkg
    cd $oldwd
}

create_tegraflash_pkg:tegra186() {
    local f
    PATH="${STAGING_BINDIR_NATIVE}/tegra186-flash:${PATH}"
    rm -rf "${WORKDIR}/tegraflash"
    mkdir -p "${WORKDIR}/tegraflash"
    oldwd=`pwd`
    cd "${WORKDIR}/tegraflash"
    cp "${STAGING_DATADIR}/tegraflash/bsp_version" .
    cp "${STAGING_DATADIR}/tegraflash/${MACHINE}.cfg" .
    cp "${IMAGE_TEGRAFLASH_KERNEL}" ./${LNXFILE}
    if [ -n "${IMAGE_TEGRAFLASH_INITRD_FLASHER}" ]; then
        cp "${IMAGE_TEGRAFLASH_INITRD_FLASHER}" ./initrd-flash.img
    fi
    if [ -n "${DATAFILE}" -a -n "${IMAGE_TEGRAFLASH_DATA}" ]; then
        cp "${IMAGE_TEGRAFLASH_DATA}" ${DATAFILE}
        DATAARGS="--datafile ${DATAFILE}"
    fi
    if [ -n "${IMAGE_TEGRAFLASH_BOOTPART}" -a -n "${BOOTPARTFILE}" ]; then
        cp "${IMAGE_TEGRAFLASH_BOOTPART}" ${BOOTPARTFILE}
	DATAARGS="$DATAARGS --bootpartfile ${BOOTPARTFILE}"
    fi
    cp -L "${DEPLOY_DIR_IMAGE}/${DTBFILE}" ./${DTBFILE}
    if [ -n "${KERNEL_ARGS}" ]; then
        fdtput -t s ./${DTBFILE} /chosen bootargs "${KERNEL_ARGS}"
    elif fdtget -t s ./${DTBFILE} /chosen bootargs >/dev/null 2>&1; then
        fdtput -d ./${DTBFILE} /chosen bootargs
    fi
    cp "${DEPLOY_DIR_IMAGE}/cboot-${MACHINE}.bin" ./${CBOOTFILENAME}
    cp "${DEPLOY_DIR_IMAGE}/tos-${MACHINE}.img" ./${TOSIMGFILENAME}
    cp "${DEPLOY_DIR_IMAGE}/bootlogo-${MACHINE}.blob" ./${BMPBLOBFILENAME}
    for f in ${BOOTFILES}; do
        cp "${STAGING_DATADIR}/tegraflash/$f" .
    done
    rm -f ./slot_metadata.bin
    cp ${STAGING_DATADIR}/tegraflash/slot_metadata.bin ./
    rm -rf ./rollback
    mkdir ./rollback
    cp -R ${STAGING_DATADIR}/nv_tegra/rollback/t${@d.getVar('NVIDIA_CHIP')[2:]}x ./rollback/
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
            cp $fname ./
        done
    done
    if [ "${TEGRA_SIGNING_EXCLUDE_TOOLS}" != "1" ]; then
        cp -R ${STAGING_BINDIR_NATIVE}/tegra186-flash/* .
        if [ -z "${IMAGE_TEGRAFLASH_INITRD_FLASHER}" ]; then
            rm -f ./initrd-flash
        fi
        mv ./rollback_parser.py ./rollback/
        tegraflash_generate_bupgen_script
    fi
    dd if=/dev/zero of=badpage.bin bs=4096 count=1
    if [ -e ${STAGING_DATADIR}/tegraflash/odmfuse_pkc_${MACHINE}.xml ]; then
        cp ${STAGING_DATADIR}/tegraflash/odmfuse_pkc_${MACHINE}.xml ./odmfuse_pkc.xml
    fi
    tegraflash_custom_pre
    cp "${IMAGE_TEGRAFLASH_ROOTFS}" ${IMAGE_BASENAME}.${IMAGE_TEGRAFLASH_FS_TYPE}
    tegraflash_create_flash_config "${WORKDIR}/tegraflash" ${LNXFILE}
    if [ "${TEGRAFLASH_ROOTFS_EXTERNAL}" = "1" ]; then
        rm -rf "${WORKDIR}/tegraflash/external" external-flash.xml.in
	mkdir "${WORKDIR}/tegraflash/external"
        tegraflash_create_flash_config "${WORKDIR}/tegraflash/external" ${LNXFILE} ${STAGING_DATADIR}/tegraflash/${PARTITION_LAYOUT_EXTERNAL}
	mv external/flash.xml.in ./external-flash.xml.in
	rm -rf external
    fi
    rm -f doflash.sh
    cat > doflash.sh <<END
#!/bin/sh
MACHINE=${MACHINE} ./tegra186-flash-helper.sh -B ${TEGRA_BLBLOCKSIZE} $DATAARGS flash.xml.in ${DTBFILE} ${MACHINE}.cfg ${ODMDATA} ${LNXFILE} ${IMAGE_BASENAME}.${IMAGE_TEGRAFLASH_FS_TYPE} "\$@"
END
    chmod +x doflash.sh
    if [ -n "${IMAGE_TEGRAFLASH_INITRD_FLASHER}" ]; then
        rm -f .env.initrd-flash
        cat > .env.initrd-flash <<END
FLASH_HELPER=${SOC_FAMILY}-flash-helper.sh
BOOTDEV="${TNSPEC_BOOTDEV}"
ROOTFS_DEVICE="${ROOTFS_DEVICE_FOR_INITRD_FLASH}"
MACHINE="${MACHINE}"
CHIPID="${NVIDIA_CHIP}"
DEFAULTS[BOARDID]="${TEGRA_BOARDID}"
DEFAULTS[FAB]="${TEGRA_FAB}"
DEFAULTS[fuselevel]="fuselevel_production"
DTBFILE="${DTBFILE}"
EMMC_BCTS="${MACHINE}.cfg"
ODMDATA="${ODMDATA}"
LNXFILE="${LNXFILE}"
ROOTFS_IMAGE="${IMAGE_BASENAME}.${IMAGE_TEGRAFLASH_FS_TYPE}"
DATAFILE="${DATAFILE}"
BOOTPARTFILE="${BOOTPARTFILE}"
EXTERNAL_ROOTFS_DRIVE=${TEGRAFLASH_ROOTFS_EXTERNAL}
BOOT_PARTITIONS_ON_EMMC=${BOOT_PARTITIONS_ON_EMMC}
END
    fi
    if [ -e ./odmfuse_pkc.xml ]; then
        cat > burnfuses.sh <<END
#!/bin/sh
MACHINE=${MACHINE} ./tegra186-flash-helper.sh -c "burnfuses odmfuse_pkc.xml" --no-flash $DATAARGS flash.xml.in ${DTBFILE} ${MACHINE}.cfg ${ODMDATA} ${LNXFILE} ${IMAGE_BASENAME}.${IMAGE_TEGRAFLASH_FS_TYPE} "\$@"
END
        chmod +x burnfuses.sh
    fi
    tegraflash_custom_post
    tegraflash_custom_sign_pkg
    tegraflash_finalize_pkg
    cd $oldwd
}

create_tegraflash_pkg:tegra194() {
    local f
    PATH="${STAGING_BINDIR_NATIVE}/tegra186-flash:${PATH}"
    rm -rf "${WORKDIR}/tegraflash"
    mkdir -p "${WORKDIR}/tegraflash"
    oldwd=`pwd`
    cd "${WORKDIR}/tegraflash"
    cp "${STAGING_DATADIR}/tegraflash/bsp_version" .
    cp "${STAGING_DATADIR}/tegraflash/${MACHINE}.cfg" .
    cp "${STAGING_DATADIR}/tegraflash/${MACHINE}-override.cfg" .
    cp "${IMAGE_TEGRAFLASH_KERNEL}" ./${LNXFILE}
    if [ -n "${IMAGE_TEGRAFLASH_INITRD_FLASHER}" ]; then
        cp "${IMAGE_TEGRAFLASH_INITRD_FLASHER}" ./initrd-flash.img
    fi
    if [ -n "${DATAFILE}" -a -n "${IMAGE_TEGRAFLASH_DATA}" ]; then
        cp "${IMAGE_TEGRAFLASH_DATA}" ./${DATAFILE}
        DATAARGS="--datafile ${DATAFILE}"
    fi
    cp -L "${DEPLOY_DIR_IMAGE}/${DTBFILE}" ./${DTBFILE}
    if [ -n "${KERNEL_ARGS}" ]; then
        fdtput -t s ./${DTBFILE} /chosen bootargs "${KERNEL_ARGS}"
    elif fdtget -t s ./${DTBFILE} /chosen bootargs >/dev/null 2>&1; then
        fdtput -d ./${DTBFILE} /chosen bootargs
    fi
    cp "${DEPLOY_DIR_IMAGE}/cboot-${MACHINE}.bin" ./${CBOOTFILENAME}
    cp "${DEPLOY_DIR_IMAGE}/tos-${MACHINE}.img" ./${TOSIMGFILENAME}
    cp "${DEPLOY_DIR_IMAGE}/bootlogo-${MACHINE}.blob" ./${BMPBLOBFILENAME}
    for f in ${BOOTFILES}; do
        cp "${STAGING_DATADIR}/tegraflash/$f" .
    done
    if [ -e "${STAGING_DATADIR}/tegraflash/cbo.dtb" ]; then
        cp "${STAGING_DATADIR}/tegraflash/cbo.dtb" .
    else
	rm -f ./cbo.dtb
    fi
    rm -f ./slot_metadata.bin
    cp ${STAGING_DATADIR}/tegraflash/slot_metadata.bin ./
    rm -rf ./rollback
    mkdir ./rollback
    cp -R ${STAGING_DATADIR}/nv_tegra/rollback/t${@d.getVar('NVIDIA_CHIP')[2:]}x ./rollback/
    cp ${STAGING_DATADIR}/tegraflash/flashvars .
    for f in ${STAGING_DATADIR}/tegraflash/tegra19[4x]-*.cfg; do
        cp $f .
    done
    for f in ${STAGING_DATADIR}/tegraflash/tegra194-*-bpmp-*.dtb; do
        cp $f .
    done
    if [ "${TEGRA_SIGNING_EXCLUDE_TOOLS}" != "1" ]; then
        cp -R ${STAGING_BINDIR_NATIVE}/tegra186-flash/* .
        if [ -z "${IMAGE_TEGRAFLASH_INITRD_FLASHER}" ]; then
            rm -f ./initrd-flash
        fi
        mv ./rollback_parser.py ./rollback/
        tegraflash_generate_bupgen_script
    fi
    if [ -e ${STAGING_DATADIR}/tegraflash/odmfuse_pkc_${MACHINE}.xml ]; then
        cp ${STAGING_DATADIR}/tegraflash/odmfuse_pkc_${MACHINE}.xml ./odmfuse_pkc.xml
    fi
    tegraflash_custom_pre
    cp "${IMAGE_TEGRAFLASH_ROOTFS}" ./${IMAGE_BASENAME}.${IMAGE_TEGRAFLASH_FS_TYPE}
    tegraflash_create_flash_config "${WORKDIR}/tegraflash" ${LNXFILE}
    if [ "${TEGRAFLASH_ROOTFS_EXTERNAL}" = "1" ]; then
        rm -rf "${WORKDIR}/tegraflash/external" external-flash.xml.in
	mkdir "${WORKDIR}/tegraflash/external"
        tegraflash_create_flash_config "${WORKDIR}/tegraflash/external" ${LNXFILE} ${STAGING_DATADIR}/tegraflash/${PARTITION_LAYOUT_EXTERNAL}
	mv external/flash.xml.in ./external-flash.xml.in
	rm -rf external
    fi
    rm -f doflash.sh
    cat > doflash.sh <<END
#!/bin/sh
MACHINE=${MACHINE} ./tegra194-flash-helper.sh $DATAARGS flash.xml.in ${DTBFILE} ${MACHINE}.cfg,${MACHINE}-override.cfg ${ODMDATA} ${LNXFILE} ${IMAGE_BASENAME}.${IMAGE_TEGRAFLASH_FS_TYPE} "\$@"
END
    chmod +x doflash.sh
    if [ -n "${IMAGE_TEGRAFLASH_INITRD_FLASHER}" ]; then
        rm -f .env.initrd-flash
        cat > .env.initrd-flash <<END
FLASH_HELPER=${SOC_FAMILY}-flash-helper.sh
BOOTDEV="${TNSPEC_BOOTDEV}"
ROOTFS_DEVICE="${ROOTFS_DEVICE_FOR_INITRD_FLASH}"
MACHINE="${MACHINE}"
CHIPID="${NVIDIA_CHIP}"
DEFAULTS[BOARDID]="${TEGRA_BOARDID}"
DEFAULTS[FAB]="${TEGRA_FAB}"
DEFAULTS[CHIPREV]="${TEGRA_CHIPREV}"
DEFAULTS[BOARDSKU]="${TEGRA_BOARDSKU}"
DEFAULTS[BOARDREV]="${TEGRA_BOARDREV}"
DEFAULTS[fuselevel]="fuselevel_production"
DTBFILE="${DTBFILE}"
EMMC_BCTS="${EMMC_BCT}${@',' + d.getVar('EMMC_BCT_OVERRIDE') if d.getVar('EMMC_BCT_OVERRIDE') else ''}"
ODMDATA="${ODMDATA}"
LNXFILE="${LNXFILE}"
ROOTFS_IMAGE="${IMAGE_BASENAME}.${IMAGE_TEGRAFLASH_FS_TYPE}"
DATAFILE="${DATAFILE}"
EXTERNAL_ROOTFS_DRIVE=${TEGRAFLASH_ROOTFS_EXTERNAL}
BOOT_PARTITIONS_ON_EMMC=${BOOT_PARTITIONS_ON_EMMC}
END
    fi
    if [ -e ./odmfuse_pkc.xml ]; then
        cat > burnfuses.sh <<END
#!/bin/sh
MACHINE=${MACHINE} ./tegra194-flash-helper.sh -c "burnfuses odmfuse_pkc.xml" --no-flash $DATAARGS flash.xml.in ${DTBFILE} ${MACHINE}.cfg,${MACHINE}-override.cfg ${ODMDATA} ${LNXFILE} ${IMAGE_BASENAME}.${IMAGE_TEGRAFLASH_FS_TYPE} "\$@"
END
        chmod +x burnfuses.sh
    fi
    if [ "${TEGRA_SPIFLASH_BOOT}" = "1" ]; then
        rm -f dosdcard.sh
        cat > dosdcard.sh <<END
#!/bin/sh
MACHINE=${MACHINE} BOARDID=\${BOARDID:-${TEGRA_BOARDID}} FAB=\${FAB:-${TEGRA_FAB}} CHIPREV=\${CHIPREV:-${TEGRA_CHIPREV}} BOARDSKU=\${BOARDSKU:-${TEGRA_BOARDSKU}} ./tegra194-flash-helper.sh --sdcard -B ${TEGRA_BLBLOCKSIZE} -s ${TEGRAFLASH_SDCARD_SIZE} -b ${IMAGE_BASENAME} $DATAARGS flash.xml.in ${DTBFILE} ${MACHINE}.cfg,${MACHINE}-override.cfg ${ODMDATA} ${LNXFILE} ${IMAGE_BASENAME}.${IMAGE_TEGRAFLASH_FS_TYPE} "\$@"
END
        chmod +x dosdcard.sh
    elif [ "${TEGRA_ROOTFS_AND_KERNEL_ON_SDCARD}" = "1" ]; then
        rm -f doflash.sh
        cat > doflash.sh <<END
#!/bin/sh
./nvflashxmlparse --split=flash-sdcard.xml.in --change-device-type=sdcard --output=flash-mmc.xml.in --sdcard-size=${TEGRAFLASH_SDCARD_SIZE} flash.xml.in
MACHINE=${MACHINE} ./tegra194-flash-helper.sh $DATAARGS flash-mmc.xml.in ${DTBFILE} ${EMMC_BCT},${EMMC_BCT_OVERRIDE} ${ODMDATA} ${LNXFILE} ${IMAGE_BASENAME}.${IMAGE_TEGRAFLASH_FS_TYPE} "\$@"
END
        chmod +x doflash.sh
        rm -f dosdcard.sh
        cat > dosdcard.sh <<END
#!/bin/sh
./nvflashxmlparse --split=flash-sdcard.xml.in --change-device-type=sdcard --output=flash-mmc.xml.in --sdcard-size=${TEGRAFLASH_SDCARD_SIZE} flash.xml.in
MACHINE=${MACHINE} BOARDID=\${BOARDID:-${TEGRA_BOARDID}} FAB=\${FAB:-${TEGRA_FAB}} CHIPREV=\${CHIPREV:-${TEGRA_CHIPREV}} BOARDSKU=\${BOARDSKU:-${TEGRA_BOARDSKU}} BOARDREV=\${BOARDREV:-${TEGRA_BOARDREV}} fuselevel=\${fuselevel:-fuselevel_production} ./tegra194-flash-helper.sh --sdcard -B ${TEGRA_BLBLOCKSIZE} -s ${TEGRAFLASH_SDCARD_SIZE} -b ${IMAGE_BASENAME} $DATAARGS flash-sdcard.xml.in ${DTBFILE} ${EMMC_BCT},${EMMC_BCT_OVERRIDE} ${ODMDATA} ${LNXFILE} ${IMAGE_BASENAME}.${IMAGE_TEGRAFLASH_FS_TYPE} "\$@"
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
        sdramcfg="${MACHINE}.cfg,${MACHINE}-override.cfg"
    else
        sdramcfg="${MACHINE}.cfg"
    fi
    fab="${TEGRA_FAB}"
    boardsku="${TEGRA_BOARDSKU}"
    boardrev="${TEGRA_BOARDREV}"
    for spec__ in ${@' '.join(['"%s"' % entry for entry in d.getVar('TEGRA_BUPGEN_SPECS').split()])}; do
        eval $spec__
        cat <<EOF >> $outfile
MACHINE=${MACHINE} FAB="$fab" BOARDSKU="$boardsku" BOARDREV="$boardrev" ./${SOC_FAMILY}-flash-helper.sh --bup ./flash-stripped.xml.in ${DTBFILE} $sdramcfg ${ODMDATA} "\$@"
EOF
    done
    chmod +x $outfile
}

IMAGE_CMD:tegraflash = "create_tegraflash_pkg"
TEGRAFLASH_PKG_DEPENDS = "${@'zip-native:do_populate_sysroot' if d.getVar('TEGRAFLASH_PACKAGE_FORMAT') == 'zip' else '${CONVERSION_DEPENDS_gz}:do_populate_sysroot'}"
TEGRAFLASH_INITRD_FLASH_DEPENDS ?= "${@tegraflash_initrd_flasher_deps(d)}"
do_image_tegraflash[depends] += "${TEGRAFLASH_PKG_DEPENDS} dtc-native:do_populate_sysroot coreutils-native:do_populate_sysroot \
                                 ${SOC_FAMILY}-flashtools-native:do_populate_sysroot gptfdisk-native:do_populate_sysroot \
                                 tegra-bootfiles:do_populate_sysroot tegra-bootfiles:do_populate_lic \
                                 tegra-redundant-boot-rollback:do_populate_sysroot virtual/kernel:do_deploy \
                                 ${@'${INITRD_IMAGE}:do_image_complete' if d.getVar('INITRD_IMAGE') != '' else  ''} \
                                 ${@'${IMAGE_UBOOT}:do_deploy ${IMAGE_UBOOT}:do_populate_lic' if d.getVar('IMAGE_UBOOT') != '' else  ''} \
                                 cboot:do_deploy virtual/secure-os:do_deploy virtual/bootlogo:do_deploy ${TEGRA_SIGNING_EXTRA_DEPS} \
                                 ${TEGRAFLASH_INITRD_FLASH_DEPENDS}"
IMAGE_TYPEDEP:tegraflash += "${IMAGE_TEGRAFLASH_FS_TYPE} ${TEGRA_BOOTPART_TYPE}"

IMAGE_CMD:tegrabootpart = "create_bootpart_image"
IMAGE_TYPEDEP:tegrabootpart = "${IMAGE_TEGRAFLASH_FS_TYPE}"

create_bootpart_image() {
    local fstype="${IMAGE_TEGRAFLASH_FS_TYPE}"
    dd if=/dev/zero of=${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.bootpart.$fstype seek=${TEGRA_BOOTPART_SIZE} count=0 bs=1024
    mkfs.$fstype -F -d ${IMAGE_ROOTFS}/boot ${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.bootpart.$fstype
    fsck.$fstype -pvfD ${IMGDEPLOYDIR}/${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.bootpart.$fstype || [ $? -le 3 ]
    ln -sf ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.bootpart.$fstype ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.bootpart.$fstype
}

oe_make_bup_payload() {
    PATH="${STAGING_BINDIR_NATIVE}/${FLASHTOOLS_DIR}:${PATH}"
    export cbootfilename=${CBOOTFILENAME}
    export tosimgfilename=${TOSIMGFILENAME}
    export bmpblobfilename=${BMPBLOBFILENAME}
    rm -rf ${WORKDIR}/bup-payload
    mkdir ${WORKDIR}/bup-payload
    oldwd="$PWD"
    cd ${WORKDIR}/bup-payload
    if [ "${SOC_FAMILY}" = "tegra186" ]; then
        dd if=/dev/zero of=badpage.bin bs=4096 count=1
    fi
    # BUP generator really wants to use 'boot.img' for the LNX
    # partition contents
    cp $1 ./boot.img
    # BUP generator must have a layout that includes kernel/DTB/etc.
    # When those partitions are stripped from the main layout, we
    # create a copy of the original with 'bupgen-' prefix, so use
    # that if present.
    local layoutsrc
    if [ -e "${STAGING_DATADIR}/tegraflash/bupgen-${PARTITION_LAYOUT_TEMPLATE}" ]; then
        layoutsrc="${STAGING_DATADIR}/tegraflash/bupgen-${PARTITION_LAYOUT_TEMPLATE}"
    fi
    tegraflash_create_flash_config "${WORKDIR}/bup-payload" boot.img "$layoutsrc"
    cp "${STAGING_DATADIR}/tegraflash/bsp_version" .
    cp "${STAGING_DATADIR}/tegraflash/${MACHINE}.cfg" .
    if [ "${SOC_FAMILY}" = "tegra194" ]; then
        cp "${STAGING_DATADIR}/tegraflash/${MACHINE}-override.cfg" .
    fi
    for dtb in ${KERNEL_DEVICETREE}; do
        dtbf=`basename $dtb`
        rm -f ./$dtbf
        cp -L "${DEPLOY_DIR_IMAGE}/$dtbf" ./$dtbf
        if [ -n "${KERNEL_ARGS}" ]; then
            fdtput -t s ./$dtbf /chosen bootargs "${KERNEL_ARGS}"
        elif fdtget -t s ./$dtbf /chosen bootargs >/dev/null 2>&1; then
            fdtput -d ./$dtbf /chosen bootargs
        fi
    done
    cp "${DEPLOY_DIR_IMAGE}/cboot-${MACHINE}.bin" ./$cbootfilename
    cp "${DEPLOY_DIR_IMAGE}/tos-${MACHINE}.img" ./$tosimgfilename
    cp "${DEPLOY_DIR_IMAGE}/bootlogo-${MACHINE}.blob" ./$bmpblobfilename
    for f in ${BOOTFILES}; do
        cp "${STAGING_DATADIR}/tegraflash/$f" .
    done
    cp ${STAGING_DATADIR}/tegraflash/flashvars .
    . ./flashvars
    if [ "${SOC_FAMILY}" = "tegra186" ]; then
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
                cp $fname ./
            done
        done
    elif [ "${SOC_FAMILY}" = "tegra194" ]; then
        for f in ${STAGING_DATADIR}/tegraflash/tegra19[4x]-*.cfg; do
            cp $f .
        done
        for f in ${STAGING_DATADIR}/tegraflash/tegra194-*-bpmp-*.dtb; do
            cp $f .
        done
	if [ -e ${STAGING_DATADIR}/tegraflash/cbo.dtb ]; then
            cp ${STAGING_DATADIR}/tegraflash/cbo.dtb .
	fi
    elif [ "${SOC_FAMILY}" = "tegra210" ]; then
        echo "TBCFILENAME=${TBCFILENAME}" >> flashvars
        echo "RCM_TBCFILENAME=${RCM_TBCFILENAME}" >> flashvars
	if [ -n "${NVIDIA_BOARD_CFG}" ]; then
	    echo "boardcfg_file=board_config_${MACHINE}.xml" >> flashvars
            cp "${STAGING_DATADIR}/tegraflash/board_config_${MACHINE}.xml" .
	fi
    fi
    if [ "${SOC_FAMILY}" != "tegra210" ]; then
        rm -f ./slot_metadata.bin
        cp ${STAGING_DATADIR}/tegraflash/slot_metadata.bin ./
        mkdir ./rollback
        cp -R ${STAGING_DATADIR}/nv_tegra/rollback/t${@d.getVar('NVIDIA_CHIP')[2:]}x ./rollback/
    fi
    if [ "${TEGRA_SIGNING_EXCLUDE_TOOLS}" != "1" ]; then
        cp -R ${STAGING_BINDIR_NATIVE}/${FLASHTOOLS_DIR}/* ./
        sed -i -e 's,^function ,,' ./l4t_bup_gen.func
        if [ "${SOC_FAMILY}" != "tegra210" ]; then
            mv ./rollback_parser.py ./rollback/
        fi
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
CONVERSION_DEPENDS_bup-payload = "${SOC_FAMILY}-flashtools-native coreutils-native tegra-bootfiles tegra-redundant-boot-rollback dtc-native virtual/bootloader:do_deploy virtual/kernel:do_deploy virtual/secure-os:do_deploy virtual/bootlogo:do_deploy ${TEGRA_SIGNING_EXTRA_DEPS}"
CONVERSION_CMD:bup-payload = "create_bup_payload_image ${type}"
IMAGE_TYPES += "cpio.gz.cboot.bup-payload"
