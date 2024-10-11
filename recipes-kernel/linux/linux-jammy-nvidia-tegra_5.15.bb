SECTION = "kernel"
SUMMARY = "Linux for Tegra kernel recipe"
DESCRIPTION = "Linux kernel from sources provided by Nvidia for Tegra processors."
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

TEGRA_UEFI_SIGNING_CLASS ??= "tegra-uefi-signing"

inherit l4t_bsp python3native ${TEGRA_UEFI_SIGNING_CLASS}
require recipes-kernel/linux/linux-yocto.inc

KERNEL_DISABLE_FW_USER_HELPER ?= "y"

# All of our device trees are out-of-tree
KERNEL_DEVICETREE:forcevariable = ""

LINUX_VERSION ?= "5.15.148"
PV = "${LINUX_VERSION}+git${SRCPV}"
FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}-${@bb.parse.vars_from_file(d.getVar('FILE', False),d)[1]}:"

LINUX_VERSION_EXTENSION ?= "-l4t-r${@'.'.join(d.getVar('L4T_VERSION').split('.')[0:2])}-1012.12"
SCMVERSION ??= "y"

SRCBRANCH = "oe4t-patches${LINUX_VERSION_EXTENSION}"
SRCREV = "8dc079d5c8c47d3954b4261473a152a8034ff7f0"
KBRANCH = "${SRCBRANCH}"
SRC_REPO = "github.com/OE4T/linux-jammy-nvidia-tegra.git;protocol=https"
KERNEL_REPO = "${SRC_REPO}"
SRC_URI = "git://${KERNEL_REPO};name=machine;branch=${KBRANCH} \
           ${@'file://localversion_auto.cfg' if d.getVar('SCMVERSION') == 'y' else ''} \
           ${@'file://disable-fw-user-helper.cfg' if d.getVar('KERNEL_DISABLE_FW_USER_HELPER') == 'y' else ''} \
           ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'file://systemd.cfg', '', d)} \
           file://disable-module-signing.cfg \
"

KBUILD_DEFCONFIG = "defconfig"
KCONFIG_MODE = "--alldefconfig"

set_scmversion() {
    if [ "${SCMVERSION}" = "y" -a -d "${S}/.git" ]; then
        head=$(git --git-dir=${S}/.git rev-parse --verify --short HEAD 2>/dev/null || true)
        [ -z "$head" ] || echo "+g$head" > ${S}/.scmversion
    fi
}
do_kernel_checkout[postfuncs] += "set_scmversion"

sign_kernel_image() {
    tegra_uefi_sbsign "$1"
    shift
    while [ $# -gt 0 ]; do
        tegra_uefi_attach_sign "$1"
        shift
    done
}

do_sign_kernel() {
    sign_kernel_image ${KERNEL_OUTPUT_DIR}/${KERNEL_IMAGETYPE}
}
do_sign_kernel[dirs] = "${B}"
do_sign_kernel[depends] += "${TEGRA_UEFI_SIGNING_TASKDEPS}"

addtask sign_kernel after do_compile before do_install

sign_bootimg() {
    tegra_uefi_attach_sign "$1"
    rm "$1"
    mv "$1.signed" "$1"
}

bootimg_from_bundled_initramfs() {
    if [ ! -z "${INITRAMFS_IMAGE}" -a "${INITRAMFS_IMAGE_BUNDLE}" = "1" ]; then
        rm -f ${WORKDIR}/initrd
        touch ${WORKDIR}/initrd
        for imageType in ${KERNEL_IMAGETYPES} ; do
            if [ "$imageType" = "fitImage" ] ; then
                continue
            fi
            initramfs_base_name=${imageType}-${INITRAMFS_NAME}
            initramfs_symlink_name=${imageType}-${INITRAMFS_LINK_NAME}
            ${STAGING_BINDIR_NATIVE}/tegra-flash/mkbootimg \
                                    --kernel $deployDir/${initramfs_base_name}.bin \
                                    --ramdisk ${WORKDIR}/initrd \
                                    --cmdline '${KERNEL_ARGS}' \
                                    --output $deployDir/${initramfs_base_name}.cboot
            sign_bootimg $deployDir/${initramfs_base_name}.cboot
            chmod 0644 $deployDir/${initramfs_base_name}.cboot
            ln -sf ${initramfs_base_name}.cboot $deployDir/${initramfs_symlink_name}.cboot
        done
    elif [  -z "${INITRAMFS_IMAGE}" ]; then
        rm -f ${WORKDIR}/initrd
        touch ${WORKDIR}/initrd
        for imageType in ${KERNEL_IMAGETYPES} ; do
            if [ "$imageType" = "fitImage" ] ; then
                continue
            fi
            baseName=$imageType-${KERNEL_IMAGE_NAME}
            ${STAGING_BINDIR_NATIVE}/tegra-flash/mkbootimg \
                                    --kernel $deployDir/${baseName}.bin \
                                    --ramdisk ${WORKDIR}/initrd \
                                    --cmdline '${KERNEL_ARGS}' \
                                    --output $deployDir/${baseName}.cboot
            sign_bootimg $deployDir/${baseName}.cboot
            chmod 0644 $deployDir/${baseName}.cboot
            ln -sf ${baseName}.cboot $deployDir/$imageType-${KERNEL_IMAGE_LINK_NAME}.cboot
            ln -sf ${baseName}.cboot $deployDir/$imageType.cboot
        done
    fi
}
do_deploy:append() {
    bootimg_from_bundled_initramfs
}

do_deploy[depends] += "tegra-flashtools-native:do_populate_sysroot ${TEGRA_UEFI_SIGNING_TASKDEPS}"

COMPATIBLE_MACHINE = "(tegra)"

RRECOMMENDS:${KERNEL_PACKAGE_NAME}-base = ""

# kernel.bbclass automatically adds a dependency on the intramfs image
# even if INITRAMFS_IMAGE_BUNDLE is disabled.  This creates a circular
# dependency for tegra builds, where we need to combine initramfs (as an
# initrd) and kernel artifacts into a bootable image, so break that
# dependency here.
python () {
    image = d.getVar('INITRAMFS_IMAGE')
    if image and not bb.utils.to_boolean(d.getVar('INITRAMFS_IMAGE_BUNDLE')):
        flags = d.getVarFlag('do_bundle_initramfs', 'depends', False).split()
        try:
            i = flags.index('${INITRAMFS_IMAGE}:do_image_complete')
            del flags[i]
            d.setVarFlag('do_bundle_initramfs', 'depends', ' '.join(flags))
        except ValueError:
            bb.warn('did not find it in %s' % ','.join(flags))
            pass
}
