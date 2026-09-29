DESCRIPTION = "Generate UEFI capsules for bup paylods"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit tegra-bup deploy image_types_tegra kernel-artifact-names

TEGRA_UEFI_CAPSULE_SIGNING_CLASS ??= "tegra-uefi-capsule-signing"
inherit ${TEGRA_UEFI_CAPSULE_SIGNING_CLASS}

TEGRA_UEFI_CAPSULE_SIGNING_EXTRA_DEPS ??= ""
TEGRA_UEFI_SYSTEM_IMAGE_TYPE_GUID ??= ""
TEGRA_UEFI_CAPSULE_ALT_IMAGE_TYPE_GUIDS ??= ""

COMPATIBLE_MACHINE = "(tegra)"

TEGRA_SIGNING_EXTRA_DEPS ??= ""

do_compile() {
    # Older versions of this recipe use GUID
    if [ -n "${GUID}" ]; then
        bberror "Please set TEGRA_SYSTEM_IMAGE_GUID to override the image type GUID"
    fi
    if [ ! -e ${DEPLOY_DIR_IMAGE}/${TEGRA_FLASHVAR_UEFI_IMAGE}.fmp-image-type-id ]; then
        bberror "Missing FMP system image type GUID file"
    fi
    this_guid=$(cat ${DEPLOY_DIR_IMAGE}/${TEGRA_FLASHVAR_UEFI_IMAGE}.fmp-image-type-id)
    if [ -n "${TEGRA_UEFI_SYSTEM_IMAGE_GUID}" -a "$this_guid" != "${TEGRA_UEFI_SYSTEM_IMAGE_GUID}" ]; then
        bbwarn "TEGRA_UEFI_SYSTEM_IMAGE_GUID variable does not match built configuration"
    fi
    # Generate BUP images
    PATH="${STAGING_BINDIR_NATIVE}/${FLASHTOOLS_DIR}:${PATH}"
    export tosimgfilename=${TOSIMGFILENAME}
    rm -rf ${B}/bup-payload
    mkdir ${B}/bup-payload
    oldwd="$PWD"
    cd ${B}/bup-payload
    # BUP generator really wants to use 'boot.img' for the LNX
    # partition contents
    tegraflash_populate_package ${IMAGE_TEGRAFLASH_KERNEL} boot.img ${@tegra_bootcontrol_overlay_list(d, bup=True)}
    tegraflash_create_flash_config flash.xml.in boot.img ${STAGING_DATADIR}/tegraflash/bupgen-internal-flash.xml
    . ./flashvars
    tegraflash_custom_sign_bup
    for bup in ${B}/bup-payload/${BUP_PAYLOAD_DIR}/*; do
	    [ -e $bup ] || continue
	    BUP_generator.py --contents --check $bup
    done
    mv ${B}/bup-payload/${BUP_PAYLOAD_DIR}/* .
    cd "$oldwd"
    # Use a separate sub-directory for each signing with a different GUID
    for guid in $this_guid ${TEGRA_UEFI_CAPSULE_ALT_IMAGE_TYPE_GUIDS}; do
	rm -rf $guid
	mkdir $guid
	cd $guid
	# Create symlinks BUP payloads with a naming expected by sign_uefi_capsules
	for f in ${B}/bup-payload/*_only_payload; do
            [ -e $f ] || continue
            sfx=$(basename $f _payload)

            ln -sf $f ${B}/${BUPFILENAME}.$sfx.bup-payload
	done

	# Generate UEFI capsules
	GUID="$guid" sign_uefi_capsules

	# Check if capsules were generated successfully
	if [ ! -e tegra-bl.cap ]; then
            bberror "tegra-bl.cap wasn't generated for GUID $guid"
	fi
	if [ -e ${B}/${BUPFILENAME}.kernel.bup_payload -a ! -e tegra-kernel.cap ]; then
            bberror "tegra-kernel.cap wasn't generated for GUID $guid"
	fi
	cd "$oldwd"
    done
    mv $this_guid/tegra-bl.cap ${B}/
    if [ -e $this_guid/tegra-kernel.cap ]; then
        mv $this_guid/tegra-kernel.cap ${B}/
    fi
}
do_compile[file-checksums] += "${TEGRA_SIGNING_FILECHECKSUMS}"

TEGRA_UEFI_CAPSULE_INSTALL_DIR ??= "/opt/nvidia/UpdateCapsule"

do_install() {
    if [ -n "${TEGRA_UEFI_CAPSULE_INSTALL_DIR}" ]; then
        install -d ${D}${TEGRA_UEFI_CAPSULE_INSTALL_DIR}
        if [ -e ${B}/tegra-bl.cap ]; then
            install -m 0644 ${B}/tegra-bl.cap ${D}${TEGRA_UEFI_CAPSULE_INSTALL_DIR}
        fi
	if [ -e ${B}/tegra-kernel.cap ]; then
            install -m 0644 ${B}/tegra-kernel.cap ${D}${TEGRA_UEFI_CAPSULE_INSTALL_DIR}
        fi
	for guid in ${TEGRA_UEFI_CAPSULE_ALT_IMAGE_TYPE_GUIDS}; do
	    install -m 0644 ${B}/$guid/tegra-bl.cap ${D}${TEGRA_UEFI_CAPSULE_INSTALL_DIR}/tegra-bl-${guid}.cap
	    [ -e ${B}/$guid/tegra-kernel.cap ] || continue
	    install -m 0644 ${B}/$guid/tegra-kernel.cap ${D}${TEGRA_UEFI_CAPSULE_INSTALL_DIR}/tegra-kernel-${guid}.cap
	done
    else
        bbnote "TEGRA_UEFI_CAPSULE_INSTALL_DIR is empty, capsules won't be installed"
    fi
}

FILES:${PN} += "${TEGRA_UEFI_CAPSULE_INSTALL_DIR}"
PACKAGE_ARCH = "${MACHINE_ARCH}"
INSANE_SKIP:${PN} += "buildpaths"

do_deploy() {
    install -d ${DEPLOYDIR}
    if [ -e ${B}/tegra-bl.cap ]; then
        BL_NAME=${TNSPEC_MACHINE}-tegra-bl.cap
        install -m 0644 ${B}/tegra-bl.cap ${DEPLOYDIR}/$BL_NAME
        ln -sf -r ${DEPLOYDIR}/$BL_NAME ${DEPLOYDIR}/tegra-bl.cap
    fi
    if [ -e ${B}/tegra-kernel.cap ]; then
        KERNEL_NAME=${TNSPEC_MACHINE}-tegra-kernel.cap
        install -m 0644 ${B}/tegra-kernel.cap ${DEPLOYDIR}/$KERNEL_NAME
        ln -sf -r ${DEPLOYDIR}/$KERNEL_NAME ${DEPLOYDIR}/tegra-kernel.cap
    fi
    altcapcount=0
    for guid in ${TEGRA_UEFI_CAPSULE_ALT_IMAGE_TYPE_GUIDS}; do
	install -m 0644 ${B}/$guid/tegra-bl.cap ${DEPLOYDIR}/${TNSPEC_MACHINE}-tegra-bl-${guid}.cap
	ln -sf ${TNSPEC_MACHINE}-tegra-bl-${guid}.cap ${DEPLOYDIR}/tegra-bl-${guid}.cap
	if [ $altcapcount -eq 0 ]; then
	    ln -sf ${TNSPEC_MACHINE}-tegra-bl-${guid}.cap ${DEPLOYDIR}/tegra-bl-alt.cap
        fi
	if [ -e ${B}/$guid/tegra-kernel.cap ]; then
	    install -m 0644 ${B}/$guid/tegra-kernel.cap ${DEPLOYDIR}/${TNSPEC_MACHINE}-tegra-kernel-${guid}.cap
	    ln -sf ${TNSPEC_MACHINE}-tegra-kernel-${guid}.cap ${DEPLOYDIR}/tegra-kernel-${guid}.cap
            if [ $altcapcount -eq 0 ]; then
	        ln -sf ${TNSPEC_MACHINE}-tegra-kernel-${guid}.cap ${DEPLOYDIR}/tegra-kernel-alt.cap
	    fi
	fi
        altcapcount=$(expr $altcapcount + 1)
    done
}

addtask deploy after do_install

do_compile[depends] += "${@bup_dependency(d)} ${TEGRA_UEFI_CAPSULE_SIGNING_EXTRA_DEPS}"
do_compile[depends] += "virtual/kernel:do_deploy tegra-flashtools-native:do_populate_sysroot dtc-native:do_populate_sysroot"
do_compile[depends] += "python3-pyyaml-native:do_populate_sysroot lz4-native:do_populate_sysroot"
do_compile[depends] += "tegra-bootfiles:do_populate_sysroot"
do_compile[depends] += "coreutils-native:do_populate_sysroot virtual/secure-os:do_deploy"
do_compile[depends] += "virtual/bootloader:do_deploy"
do_compile[depends] += "${TEGRA_SIGNING_EXTRA_DEPS} ${DTB_EXTRA_DEPS} ${TEGRA_RCM_EDK2_DEPENDS}"
