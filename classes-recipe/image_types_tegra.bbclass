inherit image_types image_types_cboot image_types_tegra_esp python3native perlnative kernel-artifact-names

TEGRA_UEFI_SIGNING_CLASS ??= "tegra-uefi-signing"
inherit ${TEGRA_UEFI_SIGNING_CLASS}
TEGRA_UEFI_USE_SIGNED_FILES ??= "false"

IMAGE_TYPES += "tegraflash"

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

def tegra_dtb_extra_deps(d):
    deps = []
    if d.getVar('PREFERRED_PROVIDER_virtual/dtb'):
        deps.append('virtual/dtb:do_populate_sysroot')
    if d.getVar('TEGRA_UEFI_USE_SIGNED_FILES') == "true":
        deps.append('tegra-uefi-keys-dtb:do_populate_sysroot')
    return ' '.join(deps)

def tegra_bootcontrol_overlay_list(d, bup=False, separator=','):
    overlays = d.getVar('TEGRA_BOOTCONTROL_OVERLAYS').split()
    if d.getVar('TEGRA_UEFI_USE_SIGNED_FILES') == "true":
        overlays.append('UefiDefaultSecurityKeys.dtbo')
        if bup and os.path.exists('UefiUpdateSecurityKeys.dtbo'):
            overlays.append('UefiUpdateSecurityKeys.dtbo')
    return separator.join(overlays)

IMAGE_ROOTFS_SIZE ?= "${@tegra_default_rootfs_size(d)}"

KERNEL_ARGS ??= ""
TEGRA_SIGNING_ARGS ??= ""
TEGRA_SIGNING_ENV ??= ""
TEGRA_SIGNING_EXCLUDE_TOOLS ??= ""
TEGRA_SIGNING_EXTRA_DEPS ??= ""
DTB_EXTRA_DEPS ??= "${@tegra_dtb_extra_deps(d)}"
EXTERNAL_KERNEL_DEVICETREE ??= "${@'${RECIPE_SYSROOT}/boot/devicetree' if d.getVar('PREFERRED_PROVIDER_virtual/dtb') else ''}"

TEGRA_BUPGEN_SPECS ??= "boardid=${TEGRA_BOARDID};fab=${TEGRA_FAB};boardrev=${TEGRA_BOARDREV};chiprev=${TEGRA_CHIPREV}"
TEGRA_BUPGEN_STRIP_IMG_NAMES ??= ""
TEGRA_BUPGEN_STRIP_CMD ??= "${@tegraflash_bupgen_strip_cmd(d)}"

DTBFILE ?= "${@os.path.basename(d.getVar('KERNEL_DEVICETREE').split()[0])}"
LNXFILE ?= "boot.img"
LNXSIZE ?= "83886080"
TEGRA_RECOVERY_KERNEL_PART_SIZE ??= "83886080"
RECROOTFSSIZE ?= "314572800"
TEGRA_EXTERNAL_DEVICE_SECTORS ??= "119537664"
TEGRA_INTERNAL_DEVICE_SECTORS ??= "119537664"

IMAGE_TEGRAFLASH_FS_TYPE ??= "ext4"
IMAGE_TEGRAFLASH_ROOTFS ?= "${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.${IMAGE_TEGRAFLASH_FS_TYPE}"
TEGRAFLASH_ROOTFS_EXTERNAL = "${@'1' if d.getVar('TNSPEC_BOOTDEV') != d.getVar('TNSPEC_BOOTDEV_DEFAULT') else '0'}"
ROOTFS_DEVICE_FOR_INITRD_FLASH = "${@tegra_rootfs_device(d)}"
TEGRAFLASH_NO_INTERNAL_STORAGE ??= "0"
OVERLAY_DTB_FILE ??= ""

TEGRA_EXT4_OPTIONS ?= ""
EXTRA_IMAGECMD:append:ext4 = " ${TEGRA_EXT4_OPTIONS}"

TEGRA_STAGED_BOOT_FIRMWARE = "${TEGRA_BOOT_FIRMWARE_FILES} eks.img badpage.bin"

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

IMAGE_TEGRAFLASH_INITRD_FLASHER ?= "${@'${DEPLOY_DIR_IMAGE}/${TEGRAFLASH_INITRD_FLASH_IMAGE}-${MACHINE}.cboot' if d.getVar('TEGRAFLASH_INITRD_FLASH_IMAGE') != '' else ''}"

TOSIMGFILENAME = "tos-optee.img"
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
    mksparse -b ${TEGRA_BLBLOCKSIZE} --fillpattern=0 "${IMAGE_TEGRAFLASH_ROOTFS}" ${IMAGE_BASENAME}.img
    cp secureflash.xml initrd-secureflash.xml
    sed -i -e"s,APPFILE_b,${IMAGE_BASENAME}.img," -e"s,APPFILE,${IMAGE_BASENAME}.img," secureflash.xml
    if [ -n "${IMAGE_TEGRAFLASH_DATA}" -a -n "${DATAFILE}" ]; then
        mksparse -b ${TEGRA_BLBLOCKSIZE} --fillpattern=0 "${IMAGE_TEGRAFLASH_DATA}" ${DATAFILE}.img
        sed -i -e"s,DATAFILE,${DATAFILE}.img," secureflash.xml
    fi
    cat > .presigning-vars <<EOF
${@'\n'.join(d.getVar("TEGRA_SIGNING_ENV").split())}
EOF
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
        rm -rf doflash.sh secureflash.sh
        find . -type d -name __pycache__ -exec rm -rf {} +
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
    local destfile="$1"
    local lnxfile="$2"
    local infile="$3"

    [ -n "$infile" ] || infile="${STAGING_DATADIR}/tegraflash/internal-flash.xml"

    sed \
        -e"s,LNXFILE_b,$lnxfile," \
        -e"s,LNXFILE,$lnxfile," -e"s,LNXSIZE,${LNXSIZE}," \
        -e"s,TOSFILE,${TOSIMGFILENAME}," \
        -e"s,EKSFILE,eks.img," \
        -e"s,RECNAME,recovery," -e"s,RECSIZE,${TEGRA_RECOVERY_KERNEL_PART_SIZE}," -e"s,RECDTB-NAME,recovery-dtb," \
        -e"/RECFILE/d" -e"/RECDTB-FILE/d" -e"/BOOTCTRL-FILE/d" \
        -e"/IST_UCODE/d" -e"/IST_BPMPFW/d" -e"/IST_ICTBIN/d" -e"/IST_TESTIMG/d" -e"/IST_RTINFO/d" \
        -e"s,APPSIZE,${ROOTFSPART_SIZE}," \
        -e"s,RECROOTFSSIZE,${RECROOTFSSIZE}," \
        -e"s,APPUUID_b,," -e"s,APPUUID,," \
	-e"s,ESP_FILE,esp.img," -e"/VARSTORE_FILE/d" \
	-e"s,EXT_NUM_SECTORS,${TEGRA_EXTERNAL_DEVICE_SECTORS}," \
	-e"s,INT_NUM_SECTORS,${TEGRA_INTERNAL_DEVICE_SECTORS}," \
	"$infile" \
        > "$destfile"
}

copy_dtbs() {
    local destination=$1
    local dtb dtbf
    if [ -n "${EXTERNAL_KERNEL_DEVICETREE}" ]; then
        for dtbcand in ${KERNEL_DEVICETREE}; do
            dtb=$(find "${EXTERNAL_KERNEL_DEVICETREE}" -name "$(basename $dtbcand)" -printf '%P' 2>/dev/null)
	    if [ -z "$dtb" ]; then
	        bbwarn "Not found in ${EXTERNAL_KERNEL_DEVICETREE}: $dtbcand"
		continue
	    fi
            dtbf=`basename $dtb`
            if [ -e $destination/$dtbf ]; then
                bbnote "Overwriting $destination/$dtbf with EXTERNAL_KERNEL_DEVICETREE content"
                rm -f $destination/$dtbf $destination/$dtbf.signed
            fi
            bbnote "Copying EXTERNAL_KERNEL_DEVICETREE entry $dtb to $destination"
            cp -L "${EXTERNAL_KERNEL_DEVICETREE}/$dtb" $destination/$dtbf
	    if ${TEGRA_UEFI_USE_SIGNED_FILES}; then
                cp -L "${EXTERNAL_KERNEL_DEVICETREE}/$dtb.signed" $destination/$dtbf.signed
	    fi
        done
    else
	for dtb in ${KERNEL_DEVICETREE}; do
            dtbf=`basename $dtb`
            if [ -e $destination/$dtbf ]; then
            bbnote "Overwriting $destination/$dtbf with KERNEL_DEVICETREE content"
            rm -f $destination/$dtbf $destination/$dtbf.signed
            fi
            bbnote "Copying KERNEL_DEVICETREE entry $dtbf to $destination"
            cp -L "${DEPLOY_DIR_IMAGE}/$dtbf" $destination/$dtbf
	    if ${TEGRA_UEFI_USE_SIGNED_FILES}; then
            cp -L "${DEPLOY_DIR_IMAGE}/$dtbf.signed" $destination/$dtbf.signed
	    fi
	done
    fi
}

copy_dtb_overlays() {
    local destination=$1
    local dtb dtbf extdtb
    local extraoverlays="${TEGRA_DCE_OVERLAY} ${@d.getVar('OVERLAY_DTB_FILE').replace(',', ' ')}"
    shift
    if [ -n "${IMAGE_TEGRAFLASH_INITRD_FLASHER}" ]; then
        extraoverlays="$extraoverlays L4TConfiguration-rcmboot.dtbo"
    fi
    if ${TEGRA_UEFI_USE_SIGNED_FILES}; then
        extraoverlays="$extraoverlays UefiDefaultSecurityKeys.dtbo"
    fi
    for dtb in "$@" ${TEGRA_PLUGIN_MANAGER_OVERLAYS} $extraoverlays; do
        dtbf=`basename $dtb`
        if [ -n "${EXTERNAL_KERNEL_DEVICETREE}" ]; then
            local extdtb=$(find "${EXTERNAL_KERNEL_DEVICETREE}" -name $dtbf -printf '%P' 2>/dev/null)
	    if [ -n "$extdtb" ]; then
	        bbnote "Copying external overlay $extdtb to $destination"
		cp -L "${EXTERNAL_KERNEL_DEVICETREE}/$extdtb" $destination/$dtbf
		continue
	    fi
	fi
	bbnote "Copying overlay $dtb to $destination"
	cp -L "${DEPLOY_DIR_IMAGE}/$dtb" $destination/$dtbf
    done
}

tegraflash_populate_package() {
    local kernelimg="$1"
    local lnxfile="$2"
    local bcoverlays="$3"
    PATH="${STAGING_BINDIR_NATIVE}/${FLASHTOOLS_DIR}:$PATH"

    cp "${STAGING_DATADIR}/tegraflash/bsp_version" .
    cp "${STAGING_DATADIR}/tegraflash/${EMMC_BCT}" .
    if [ -n "${EMMC_BCT_OVERRIDE}" ]; then
        cp "${STAGING_DATADIR}/tegraflash/${EMMC_BCT_OVERRIDE}" .
    fi
    cp "$kernelimg" ./$lnxfile
    if [ -n "${DATAFILE}" -a -n "${IMAGE_TEGRAFLASH_DATA}" ]; then
        cp "${IMAGE_TEGRAFLASH_DATA}" ./${DATAFILE}
        DATAARGS="--datafile ${DATAFILE}"
    fi
    cp "${DEPLOY_DIR_IMAGE}/uefi_jetson.bin" ./uefi_jetson.bin
    cp "${DEPLOY_DIR_IMAGE}/tos-${MACHINE}.img" ./${TOSIMGFILENAME}
    for f in ${TEGRA_STAGED_BOOT_FIRMWARE}; do
        cp "${STAGING_DATADIR}/tegraflash/$f" .
    done
    sed -e "\$a\BOOTCONTROL_OVERLAYS=\"$bcoverlays\"" ${STAGING_DATADIR}/tegraflash/flashvars > ./flashvars
    rm -rf ./rollback
    mkdir ./rollback
    if [ "${SOC_FAMILY}" = "tegra234" ]; then
        cp ${STAGING_DATADIR}/tegraflash/bpmp_t234-*.bin .
        cp ${STAGING_DATADIR}/tegraflash/tegra234-*.dts* .
        cp ${STAGING_DATADIR}/tegraflash/fuse_t234.xml .
        cp ${STAGING_DATADIR}/tegraflash/tegra234-bpmp-*.dtb .
    fi

    copy_dtbs .
    local bcos="$(echo "$bcoverlays" | sed -e's!,! !g')"
    copy_dtb_overlays . $bcos
    if [ "${TEGRA_SIGNING_EXCLUDE_TOOLS}" != "1" ]; then
        cp -R ${STAGING_BINDIR_NATIVE}/${FLASHTOOLS_DIR}/* .
	if [ -z "${IMAGE_TEGRAFLASH_INITRD_FLASHER}" ]; then
	    rm -f ./initrd-flash
	fi
	if [ -d rollback ]; then
            mv rollback_parser.py ./rollback/
        else
            rm -f rollback_parser.py
        fi
        sed -i -e 's,^function ,,' ./l4t_bup_gen.func
        tegraflash_generate_bupgen_script
    fi
    if [ -e ${STAGING_DATADIR}/tegraflash/odmfuse_pkc_${MACHINE}.xml ]; then
        cp ${STAGING_DATADIR}/tegraflash/odmfuse_pkc_${MACHINE}.xml ./odmfuse_pkc.xml
    fi
}

create_tegraflash_pkg() {
    local oldwd="$PWD"
    local has_sdcard="no"

    rm -rf ${WORKDIR}/tegraflash
    mkdir -p ${WORKDIR}/tegraflash
    cd ${WORKDIR}/tegraflash
    tegraflash_populate_package ${IMAGE_TEGRAFLASH_KERNEL} ${LNXFILE} ${@tegra_bootcontrol_overlay_list(d)}
    cp "${IMAGE_TEGRAFLASH_ESPIMG}" ./esp.img
    if [ -n "${IMAGE_TEGRAFLASH_INITRD_FLASHER}" ]; then
        cp "${IMAGE_TEGRAFLASH_INITRD_FLASHER}" ./initrd-flash.img
    fi
    tegraflash_custom_pre
    cp "${IMAGE_TEGRAFLASH_ROOTFS}" ./${IMAGE_BASENAME}.${IMAGE_TEGRAFLASH_FS_TYPE}
    tegraflash_create_flash_config flash.xml.in ${LNXFILE}
    if grep -Eq '^[[:space:]]+<device type="sdcard"' flash.xml.in; then
        has_sdcard="yes"
    fi
    if [ "${TEGRAFLASH_ROOTFS_EXTERNAL}" = "1" ]; then
        tegraflash_create_flash_config external-flash.xml.in ${LNXFILE} ${STAGING_DATADIR}/tegraflash/external-flash.xml
    fi
    rm -f doflash.sh
    cat > doflash.sh <<END
#!/bin/sh
MACHINE=${TNSPEC_MACHINE} ./tegra-flash-helper.sh $DATAARGS flash.xml.in ${DTBFILE} ${EMMC_BCTS} ${ODMDATA} ${LNXFILE} ${IMAGE_BASENAME}.${IMAGE_TEGRAFLASH_FS_TYPE} "\$@"
END
    chmod +x doflash.sh

    if [ -n "${IMAGE_TEGRAFLASH_INITRD_FLASHER}" ]; then
        rm -f .env.initrd-flash
	cat > .env.initrd-flash <<END
FLASH_HELPER=tegra-flash-helper.sh
BOOTDEV="${TNSPEC_BOOTDEV}"
ROOTFS_DEVICE="${ROOTFS_DEVICE_FOR_INITRD_FLASH}"
CHIPID="${NVIDIA_CHIP}"
MACHINE="${TNSPEC_MACHINE}"
DEFAULTS[BOARDID]="${TEGRA_BOARDID}"
DEFAULTS[FAB]="${TEGRA_FAB}"
DEFAULTS[CHIPREV]="${TEGRA_CHIPREV}"
DEFAULTS[BOARDSKU]="${TEGRA_BOARDSKU}"
DEFAULTS[BOARDREV]="${TEGRA_BOARDREV}"
DTBFILE="${DTBFILE}"
EMMC_BCTS="${EMMC_BCTS}"
ODMDATA="${ODMDATA}"
LNXFILE="${LNXFILE}"
ROOTFS_IMAGE="${IMAGE_BASENAME}.${IMAGE_TEGRAFLASH_FS_TYPE}"
DATAFILE="${DATAFILE}"
EXTERNAL_ROOTFS_DRIVE=${TEGRAFLASH_ROOTFS_EXTERNAL}
NO_INTERNAL_STORAGE=${TEGRAFLASH_NO_INTERNAL_STORAGE}
END
    fi
    if [ -e ./odmfuse_pkc.xml ]; then
        cat > burnfuses.sh <<END
#!/bin/sh
MACHINE=${TNSPEC_MACHINE} ./tegra-flash-helper.sh -c "burnfuses odmfuse_pkc.xml" --no-flash $DATAARGS flash.xml.in ${DTBFILE} ${EMMC_BCTS} ${ODMDATA} ${LNXFILE} ${IMAGE_BASENAME}.${IMAGE_TEGRAFLASH_FS_TYPE} "\$@"
END
        chmod +x burnfuses.sh
    fi
    if [ "$has_sdcard" = "yes" ]; then
        rm -f dosdcard.sh
        cat > dosdcard.sh <<END
#!/bin/sh
MACHINE=${TNSPEC_MACHINE} BOARDID=\${BOARDID:-${TEGRA_BOARDID}} FAB=\${FAB:-${TEGRA_FAB}} CHIPREV=\${CHIPREV:-${TEGRA_CHIPREV}} BOARDSKU=\${BOARDSKU:-${TEGRA_BOARDSKU}} ./tegra-flash-helper.sh --sdcard -B ${TEGRA_BLBLOCKSIZE} -s ${TEGRAFLASH_SDCARD_SIZE} -b ${IMAGE_BASENAME} $DATAARGS flash.xml.in ${DTBFILE} ${EMMC_BCTS} ${ODMDATA} ${LNXFILE} ${IMAGE_BASENAME}.${IMAGE_TEGRAFLASH_FS_TYPE} "\$@"
END
        chmod +x dosdcard.sh
    fi
    if [ "${TEGRAFLASH_ROOTFS_EXTERNAL}" = "1" ]; then
        rm -f doexternal.sh
        cat > doexternal.sh <<END
#!/bin/sh
MACHINE=${TNSPEC_MACHINE} BOARDID=\${BOARDID:-${TEGRA_BOARDID}} FAB=\${FAB:-${TEGRA_FAB}} CHIPREV=\${CHIPREV:-${TEGRA_CHIPREV}} BOARDSKU=\${BOARDSKU:-${TEGRA_BOARDSKU}} ./tegra-flash-helper.sh --sdcard -B ${TEGRA_BLBLOCKSIZE} -b ${IMAGE_BASENAME} $DATAARGS --external-device external-flash.xml.in ${DTBFILE} ${EMMC_BCTS} ${ODMDATA} ${LNXFILE} ${IMAGE_BASENAME}.${IMAGE_TEGRAFLASH_FS_TYPE} "\$@"
END
        chmod +x doexternal.sh
    fi
    tegraflash_custom_post
    tegraflash_custom_sign_pkg
    tegraflash_finalize_pkg
    cd "$oldwd"
}
create_tegraflash_pkg[vardepsexclude] += "DATETIME"

def tegraflash_bupgen_strip_cmd(d):
    images = d.getVar('TEGRA_BUPGEN_STRIP_IMG_NAMES').split()
    if len(images) == 0:
        return 'cp flash.xml.in flash-stripped.xml.in'
    return 'sed {} flash.xml.in > flash-stripped.xml.in'.format(' '.join(['-e"/<filename>.*{}/d"'.format(img) for img in images]))


tegraflash_generate_bupgen_script() {
    local outfile="${1:-./generate_bup_payload.sh}"
    local spec__ fab boardsku boardrev bup_type buptype_arg
    rm -f $outfile
    cat <<EOF > $outfile
#!/bin/bash
${TEGRA_BUPGEN_STRIP_CMD}
rm -rf signed multi_signed rollback.bin ${BUP_PAYLOAD_DIR}
export BOARDID=${TEGRA_BOARDID}
export localbootfile=${LNXFILE}
export CHIPREV=${TEGRA_CHIPREV}
export CHIPID=${NVIDIA_CHIP}
EOF
    fab="${TEGRA_FAB}"
    boardsku="${TEGRA_BOARDSKU}"
    boardrev="${TEGRA_BOARDREV}"
    for spec__ in ${@' '.join(['"%s"' % entry for entry in d.getVar('TEGRA_BUPGEN_SPECS').split()])}; do
        bup_type=""
        eval $spec__
        if [ -n "$bup_type" ]; then
            buptype_arg="--bup-type $bup_type"
        else
            buptype_arg=""
        fi
        cat <<EOF >> $outfile
MACHINE=${TNSPEC_MACHINE} FAB="$fab" BOARDSKU="$boardsku" BOARDREV="$boardrev" CHIP_SKU="$chipsku" ./tegra-flash-helper.sh --sign --bup $buptype_arg ./flash-stripped.xml.in ${DTBFILE} ${EMMC_BCTS} ${ODMDATA} "\$@"
EOF
    done
    chmod +x $outfile
}

IMAGE_CMD:tegraflash = "create_tegraflash_pkg"
TEGRAFLASH_PKG_DEPENDS = "${@'zip-native:do_populate_sysroot' if d.getVar('TEGRAFLASH_PACKAGE_FORMAT') == 'zip' else '${CONVERSION_DEPENDS_gz}:do_populate_sysroot'}"
do_image_tegraflash[depends] += "${TEGRAFLASH_PKG_DEPENDS} dtc-native:do_populate_sysroot coreutils-native:do_populate_sysroot \
                                 tegra-flashtools-native:do_populate_sysroot gptfdisk-native:do_populate_sysroot \
                                 tegra-bootfiles:do_populate_sysroot tegra-bootfiles:do_populate_lic \
                                 virtual/kernel:do_deploy \
                                 ${@'${INITRD_IMAGE}:do_image_complete' if d.getVar('INITRD_IMAGE') != '' else  ''} \
                                 ${@'${TEGRA_ESP_IMAGE}:do_image_complete' if d.getVar('TEGRA_ESP_IMAGE') != '' else  ''} \
                                 virtual/bootloader:do_deploy virtual/secure-os:do_deploy ${TEGRA_SIGNING_EXTRA_DEPS} ${DTB_EXTRA_DEPS} \
                                 ${@'${TEGRAFLASH_INITRD_FLASH_IMAGE}:do_image_complete' if d.getVar('TEGRAFLASH_INITRD_FLASH_IMAGE') != '' else ''}"
IMAGE_TYPEDEP:tegraflash += "${IMAGE_TEGRAFLASH_FS_TYPE}"
