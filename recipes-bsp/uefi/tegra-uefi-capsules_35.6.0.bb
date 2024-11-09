DESCRIPTION = "Generate UEFI capsules for bup paylods"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit tegra-bup deploy image_types_tegra kernel-artifact-names

TEGRA_UEFI_CAPSULE_SIGNING_CLASS ??= "tegra-uefi-capsule-signing"
inherit ${TEGRA_UEFI_CAPSULE_SIGNING_CLASS}

TEGRA_UEFI_CAPSULE_SIGNING_EXTRA_DEPS ??= ""

COMPATIBLE_MACHINE = "(tegra)"

TEGRA_SIGNING_EXTRA_DEPS ??= ""

GUID:tegra194 ?= "be3f5d68-7654-4ed2-838c-2a2faf901a78"
GUID:tegra234 ?= "bf0d4599-20d4-414e-b2c5-3595b1cda402"

do_compile() {
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
    mv generate_bup_payload.sh doflash.sh
    tegraflash_create_flash_config flash.xml.in boot.img ${STAGING_DATADIR}/tegraflash/bupgen-internal-flash.xml
    . ./flashvars
    tegraflash_custom_sign_bup
    for bup in ${B}/bup-payload/${BUP_PAYLOAD_DIR}/*; do
	    [ -e $bup ] || continue
	    BUP_generator.py --contents --check $bup
    done
    mv ${B}/bup-payload/${BUP_PAYLOAD_DIR}/* .
    cd "$oldwd"

    # Create symlinks BUP payloads with a naming expected by sign_uefi_capsules
    for f in ${B}/bup-payload/*_only_payload; do
        [ -e $f ] || continue
        sfx=$(basename $f _payload)

        ln -sf $f ${B}/${BUPFILENAME}.$sfx.bup-payload
    done

    # Generate UEFI capsules
    sign_uefi_capsules

    # Check if capsules were generated successfully
    if [ ! -e ${B}/tegra-bl.cap ]; then
        bberror "${B}/tegra-bl.cap wasn't generated"
    fi
    if [ -e ${B}/${BUPFILENAME}.kernel.bup_payload -a ! -e ${B}/tegra-kernel.cap ]; then
        bberror "${B}/tegra-kernel.cap wasn't generated"
    fi
}

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
        ln -s -r ${DEPLOYDIR}/$BL_NAME ${DEPLOYDIR}/tegra-bl.cap
    fi
    if [ -e ${B}/tegra-kernel.cap ]; then
        KERNEL_NAME=${TNSPEC_MACHINE}-tegra-kernel.cap
        install -m 0644 ${B}/tegra-kernel.cap ${DEPLOYDIR}/$KERNEL_NAME
        ln -s -r ${DEPLOYDIR}/$KERNEL_NAME ${DEPLOYDIR}/tegra-kernel.cap
    fi
}

addtask deploy after do_install

do_compile[depends] += "${@bup_dependency(d)} ${TEGRA_UEFI_CAPSULE_SIGNING_EXTRA_DEPS}"
do_compile[depends] += "virtual/kernel:do_deploy tegra-flashtools-native:do_populate_sysroot dtc-native:do_populate_sysroot"
do_compile[depends] += "python3-pyyaml-native:do_populate_sysroot lz4-native:do_populate_sysroot"
do_compile[depends] += "tegra-redundant-boot-rollback:do_populate_sysroot tegra-bootfiles:do_populate_sysroot"
do_compile[depends] += "coreutils-native:do_populate_sysroot virtual/secure-os:do_deploy"
do_compile[depends] += "virtual/bootloader:do_deploy"
do_compile[depends] += "${TEGRA_SIGNING_EXTRA_DEPS} ${DTB_EXTRA_DEPS}"
