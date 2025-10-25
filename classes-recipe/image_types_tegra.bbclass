inherit image_types image_types_cboot image_types_tegra_esp python3native perlnative kernel-artifact-names

TEGRA_UEFI_SIGNING_CLASS ??= "tegra-uefi-signing"
inherit ${TEGRA_UEFI_SIGNING_CLASS}
TEGRA_UEFI_USE_SIGNED_FILES ??= "false"

IMAGE_TYPES += "tegraflash.tar"
CONVERSIONTYPES =+ "simg"

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
        deps.append('tegra-uefi-keys-dtb:do_deploy')
    return ' '.join(deps)

def tegra_bootcontrol_overlay_list(d, bup=False, separator=','):
    overlays = d.getVar('TEGRA_BOOTCONTROL_OVERLAYS').split()
    if d.getVar('TEGRA_UEFI_USE_SIGNED_FILES') == "true":
        overlays.append('UefiDefaultSecurityKeys.dtbo')
        if bup and os.path.exists(os.path.join(d.getVar('DEPLOY_DIR_IMAGE'), 'UefiUpdateSecurityKeys.dtbo')):
            overlays.append('UefiUpdateSecurityKeys.dtbo')
    return separator.join(overlays)

def tegra_signing_filechecksums(d):
    files = []
    if d.getVar('TEGRA_SIGNING_PKC'):
        files.append('${TEGRA_SIGNING_PKC}')
    if d.getVar('TEGRA_SIGNING_SBK'):
        files.append('${TEGRA_SIGNING_SBK}')
    if len(files) == 0:
        return ''
    return ' '.join([f + ':True' for f in files])

def tegra_signing_args(d):
    import os
    import bb

    args = ''
    pkc = d.getVar('TEGRA_SIGNING_PKC')
    if pkc:
        if not os.path.exists(pkc):
            bb.fatal("Signing file does not exist: %s" % pkc)
        args += ' -u ${TEGRA_SIGNING_PKC}'
    sbk = d.getVar('TEGRA_SIGNING_SBK')
    if sbk:
        if not os.path.exists(sbk):
            bb.fatal("Signing file does not exist: %s" % sbk)
        args += ' -v ${TEGRA_SIGNING_SBK}'
    return args

IMAGE_ROOTFS_SIZE ?= "${@tegra_default_rootfs_size(d)}"

KERNEL_ARGS ??= ""
TEGRA_SIGNING_PKC ??= ""
TEGRA_SIGNING_SBK ??= ""
TEGRA_SIGNING_ARGS ??= "${@tegra_signing_args(d)}"
TEGRA_SIGNING_ALWAYS ??= "0"
TEGRA_SIGNING_FILECHECKSUMS ??= "${@tegra_signing_filechecksums(d)}"
TEGRA_SIGNING_ENV ??= ""
TEGRA_SIGNING_EXCLUDE_TOOLS ??= ""
TEGRA_SIGNING_EXTRA_DEPS ??= ""
DTB_EXTRA_DEPS ??= "${@tegra_dtb_extra_deps(d)}"
EXTERNAL_KERNEL_DEVICETREE ??= "${@'${RECIPE_SYSROOT}/boot/devicetree' if d.getVar('PREFERRED_PROVIDER_virtual/dtb') else ''}"

TEGRA_BUPGEN_SPECS ??= "boardid=${TEGRA_BOARDID};fab=${TEGRA_FAB};boardrev=${TEGRA_BOARDREV};chiprev=${TEGRA_CHIPREV}"
TEGRA_BUPGEN_STRIP_IMG_NAMES ??= ""
TEGRA_BUPGEN_STRIP_CMD ??= "${@tegraflash_bupgen_strip_cmd(d)}"

LNXFILE ?= "boot.img"
LNXSIZE ?= "83886080"
TEGRA_RECOVERY_KERNEL_PART_SIZE ??= "83886080"
RECROOTFSSIZE ?= "314572800"
TEGRA_EXTERNAL_DEVICE_SECTORS ??= "119537664"
TEGRA_INTERNAL_DEVICE_SECTORS ??= "119537664"

IMAGE_TEGRAFLASH_FS_TYPE ??= "ext4.simg"
IMAGE_TEGRAFLASH_ROOTFS ?= "${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.${IMAGE_TEGRAFLASH_FS_TYPE}"
TEGRAFLASH_ROOTFS_EXTERNAL = "${@'1' if d.getVar('TNSPEC_BOOTDEV') != d.getVar('TNSPEC_BOOTDEV_DEFAULT') else '0'}"
ROOTFS_DEVICE_FOR_INITRD_FLASH = "${@tegra_rootfs_device(d)}"
TEGRAFLASH_NO_INTERNAL_STORAGE ??= "0"
OVERLAY_DTB_FILE ??= ""

TEGRA_EXT4_OPTIONS ?= ""
EXTRA_IMAGECMD:append:ext4 = " ${TEGRA_EXT4_OPTIONS}"

TEGRA_STAGED_BOOT_FIRMWARE = "${TEGRA_BOOT_FIRMWARE_FILES} eks.img badpage.bin"
TEGRA_STAGED_BOOT_FIRMWARE:tegra264 = "${TEGRA_BOOT_FIRMWARE_FILES} eks.img"

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
ESP_FILE ?= "${@'esp.img' if d.getVar('TEGRA_ESP_IMAGE') else ''}"
DATAFILE ??= ""
IMAGE_TEGRAFLASH_DATA ??= ""

IMAGE_TEGRAFLASH_INITRD_FLASHER ?= "${@'${DEPLOY_DIR_IMAGE}/${TEGRAFLASH_INITRD_FLASH_IMAGE}-${MACHINE}.cboot' if d.getVar('TEGRAFLASH_INITRD_FLASH_IMAGE') != '' else ''}"

TOSIMGFILENAME = "tos-optee.img"
TOSIMGFILENAME:tegra234 = "tos-optee_t234.img"
TOSIMGFILENAME:tegra264 = "tos-optee_t264.img"

BUP_PAYLOAD_DIR = "payloads_t${@d.getVar('NVIDIA_CHIP')[2:]}x"
FLASHTOOLS_DIR = "tegra-flash"

TEGRAFLASH_SDCARD_SIZE ??= "16G"

# Override this function if you need to add
# customization after the default files are
# copied/symlinked into the working directory
# and before processing begins.
tegraflash_custom_pre() {
    :
}

tegraflash_post_sign_pkg() {
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
# By default, if TEGRA_SIGNING_ARGS is defined, or
# TEGRA_SIGNING_ALWAYS is set to "1", this function
# will run the flash helper to sign the binaries.
tegraflash_custom_sign_pkg() {
    if [ -z "${TEGRA_SIGNING_ARGS}" -a "${TEGRA_SIGNING_ALWAYS}" != "1" ]; then
        return 0
    fi
    ${TEGRA_SIGNING_ENV} MACHINE=${TNSPEC_MACHINE} ./tegra-flash-helper.sh --sign --no-flash ${TEGRA_SIGNING_ARGS} flash.xml.in ${LNXFILE} ${IMAGE_BASENAME}.${IMAGE_TEGRAFLASH_FS_TYPE}
    mv secureflash.xml internal-secureflash.xml
    mv flash.idx internal-flash.idx
    # Note that with recent hardware and BSP versions, all signed
    # firmware is confined to the internal QSPI flash. (A different
    # process is used for signing binaries loaded by the UEFI
    # bootloader.) However, we still run through the flashing script
    # here so that the .idx file used by NVIDIA's flashing tools
    # gets generated.
    if [ -e external-flash.xml.in ]; then
        ${TEGRA_SIGNING_ENV} MACHINE=${TNSPEC_MACHINE} ./tegra-flash-helper.sh --sign --no-flash --external-device ${TEGRA_SIGNING_ARGS} external-flash.xml.in ${LNXFILE} ${IMAGE_BASENAME}.${IMAGE_TEGRAFLASH_FS_TYPE}
	mv secureflash.xml external-secureflash.xml
	mv flash.idx external-flash.idx
    fi
    if [ -e rcmboot-flash.xml.in -a -n "${IMAGE_TEGRAFLASH_INITRD_FLASHER}" ]; then
        ${TEGRA_SIGNING_ENV} MACHINE=${TNSPEC_MACHINE} ./tegra-flash-helper.sh --no-flash --rcm-boot ${TEGRA_SIGNING_ARGS} rcmboot-flash.xml.in initrd-flash.img ${IMAGE_BASENAME}.${IMAGE_TEGRAFLASH_FS_TYPE}
	[ -d rcmboot_blob ] || bberror "RCM boot blob was not created"
    fi
    find . -type d -name __pycache__ -exec rm -rf {} +
    tegraflash_post_sign_pkg
}

# -- Function for BUP signing/creation --
# Note that this is *always* run. If no key is provided, binaries
# will be signed with a null key.
tegraflash_custom_sign_bup() {
    ./generate_bup_payload.sh ${TEGRA_SIGNING_ARGS}
}

# Override this function if you need to add
# customization after other processing is done
# but before the zip package is created.
tegraflash_custom_post() {
    :
}

tegraflash_finalize_pkg() {
    rm -f ${IMGDEPLOYDIR}/${IMAGE_NAME}.tegraflash.tar
    ${IMAGE_CMD_TAR} --sparse --numeric-owner --transform="s,^\./,," -cf ${IMGDEPLOYDIR}/${IMAGE_NAME}.tegraflash.tar .
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
        -e"/IST_UCODE/d" -e"/IST_BPMPFW/d" -e"/IST_ICTBIN/d" -e"/IST_TESTIMG/d" -e"/IST_RTINFO/d" -e"/IST_RTID/d" \
        -e"s,APPSIZE,${ROOTFSPART_SIZE}," \
        -e"s,RECROOTFSSIZE,${RECROOTFSSIZE}," \
        -e"s,APPUUID_b,," -e"s,APPUUID,," \
	-e"s,ESP_FILE,${ESP_FILE}," -e"/VARSTORE_FILE/d" \
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
    cp "${STAGING_DATADIR}/tegraflash/${EMC_BCT}" .
    cp "$kernelimg" ./$lnxfile
    if [ -n "${DATAFILE}" -a -n "${IMAGE_TEGRAFLASH_DATA}" ]; then
        cp "${IMAGE_TEGRAFLASH_DATA}" ./${DATAFILE}
        DATAARGS="--datafile ${DATAFILE}"
    fi
    if [ "${SOC_FAMILY}" = "tegra234" ]; then
        cp "${DEPLOY_DIR_IMAGE}/uefi_t23x_general.bin" ./uefi_t23x_general.bin
    fi
    if [ "${SOC_FAMILY}" = "tegra264" ]; then
        cp "${DEPLOY_DIR_IMAGE}/uefi_t26x_general.bin" ./uefi_t26x_general.bin
        cp "${DEPLOY_DIR_IMAGE}/standalonemm_jetson.pkg" ./standalonemm_jetson.pkg
        cp "${DEPLOY_DIR_IMAGE}/hafnium_t264.fip" ./hafnium_t264.fip
    fi
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
    elif [ "${SOC_FAMILY}" = "tegra264" ]; then
        cp ${STAGING_DATADIR}/tegraflash/bpmp_t264-*.bin .
        cp ${STAGING_DATADIR}/tegraflash/tegra264* .
        cp ${STAGING_DATADIR}/tegraflash/t264* .
        cp ${STAGING_DATADIR}/tegraflash/fuse_t264.xml .
        cp ${STAGING_DATADIR}/tegraflash/tegra264-bpmp-*.dtb .
        cp ${STAGING_DATADIR}/tegraflash/platform_config_profile.yaml .
    fi

    copy_dtbs .
    local bcos="$(echo "$bcoverlays" | sed -e's!,! !g')"
    copy_dtb_overlays . $bcos
    if [ "${TEGRA_SIGNING_EXCLUDE_TOOLS}" != "1" ]; then
        cp -R ${STAGING_BINDIR_NATIVE}/${FLASHTOOLS_DIR}/* .
	if [ -z "${IMAGE_TEGRAFLASH_INITRD_FLASHER}" ]; then
	    rm -f ./initrd-flash
	fi
        sed -i -e 's,^function ,,' ./l4t_bup_gen.func
        tegraflash_generate_bupgen_script
    fi
    # See generate_unified_flash_files function in l4t_initrd_flash_internal.sh
    if [ -d ./unified_flash/tools/flashtools/flash ]; then
        local f
        # XXX losetup and e2fsprogs, along with adbd, need to go into
        # our flashing initrd
        for f in resize2fs losetup e2fsck dumpe2fs flash_lz4 tegrakeyhash xmss-sign tegrasign_v3_nvkey_load.py tegrasign_v3_nvkey.yaml t234_sbk_dev.key t234_rsa_dev.key; do
            touch ./unified_flash/tools/flashtools/flash/$f
        done
    fi
}

create_tegraflash_pkg() {
    local oldwd="$PWD"
    local has_sdcard="no"

    rm -rf ${WORKDIR}/tegraflash
    mkdir -p ${WORKDIR}/tegraflash
    cd ${WORKDIR}/tegraflash
    tegraflash_populate_package ${IMAGE_TEGRAFLASH_KERNEL} ${LNXFILE} ${@tegra_bootcontrol_overlay_list(d)}
    if [ -n "${ESP_FILE}" ]; then
        cp "${IMAGE_TEGRAFLASH_ESPIMG}" ./${ESP_FILE}
    fi
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
    if [ -n "${PARTITION_LAYOUT_RCMBOOT}" ]; then
        tegraflash_create_flash_config rcmboot-flash.xml.in ${LNXFILE} ${STAGING_DATADIR}/tegraflash/${PARTITION_LAYOUT_RCMBOOT}
    fi

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
LNXFILE="${LNXFILE}"
ROOTFS_IMAGE="${IMAGE_BASENAME}.${IMAGE_TEGRAFLASH_FS_TYPE}"
DATAFILE="${DATAFILE}"
EXTERNAL_ROOTFS_DRIVE=${TEGRAFLASH_ROOTFS_EXTERNAL}
NO_INTERNAL_STORAGE=${TEGRAFLASH_NO_INTERNAL_STORAGE}
END
    fi
    tegraflash_custom_post
    tegraflash_custom_sign_pkg
    tegraflash_finalize_pkg
    cd "$oldwd"
}
create_tegraflash_pkg[vardepsexclude] += "DATETIME"
do_image_tegraflash_tar[file-checksums] += "${TEGRA_SIGNING_FILECHECKSUMS}"

def tegraflash_bupgen_strip_cmd(d):
    images = d.getVar('TEGRA_BUPGEN_STRIP_IMG_NAMES').split()
    esp_file = d.getVar('ESP_FILE')
    if esp_file:
        images.append(esp_file)
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
MACHINE=${TNSPEC_MACHINE} FAB="$fab" BOARDSKU="$boardsku" BOARDREV="$boardrev" CHIP_SKU="$chipsku" ./tegra-flash-helper.sh --sign --bup $buptype_arg ./flash-stripped.xml.in "\$@"
EOF
    done
    chmod +x $outfile
}

tegra_mksparse() {
    PATH="${STAGING_BINDIR_NATIVE}/${FLASHTOOLS_DIR}:$PATH"
    mksparse -b ${TEGRA_BLBLOCKSIZE} --fillpattern=0 "$1" "$2"
}

IMAGE_CMD:tegraflash.tar = "create_tegraflash_pkg"
do_image_tegraflash_tar[depends] += "dtc-native:do_populate_sysroot coreutils-native:do_populate_sysroot \
                                 tegra-flashtools-native:do_populate_sysroot gptfdisk-native:do_populate_sysroot \
                                 tegra-bootfiles:do_populate_sysroot tegra-bootfiles:do_populate_lic \
                                 virtual/kernel:do_deploy \
                                 ${@'${INITRD_IMAGE}:do_image_complete' if d.getVar('INITRD_IMAGE') != '' else  ''} \
                                 ${@'${TEGRA_ESP_IMAGE}:do_image_complete' if d.getVar('TEGRA_ESP_IMAGE') != '' else  ''} \
                                 virtual/secure-os:do_deploy ${TEGRA_SIGNING_EXTRA_DEPS} ${DTB_EXTRA_DEPS} \
                                 ${@'${TEGRAFLASH_INITRD_FLASH_IMAGE}:do_image_complete' if d.getVar('TEGRAFLASH_INITRD_FLASH_IMAGE') != '' else ''}"
# XXX-
# Image type dependencies in OE-Core don't completely handle image
# types with '.' in them correctly, so we need to specify these twice.
#
# The anonymous python in image.bbclass allows 'xxx.yyy' as an image
# type, and translates the '.' to '_' where needed.
# The imagetypes_getdepends() function in image_types.bbclass always
# splits on '.' and only looks at IMAGE_TYPEDEP:xxx
IMAGE_TYPEDEP:tegraflash += "${IMAGE_TEGRAFLASH_FS_TYPE}"
IMAGE_TYPEDEP:tegraflash.tar += "${IMAGE_TEGRAFLASH_FS_TYPE}"
# -XXX
CONVERSION_CMD:simg = "tegra_mksparse ${IMAGE_NAME}.${type} ${IMAGE_NAME}.${type}.simg"
CONVERSION_DEPENDS_simg = "tegra-flashtools-native"
