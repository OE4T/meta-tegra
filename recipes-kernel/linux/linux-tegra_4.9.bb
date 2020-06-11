SECTION = "kernel"
SUMMARY = "Linux for Tegra kernel recipe"
DESCRIPTION = "Linux kernel from sources provided by Nvidia for Tegra processors."
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

inherit l4t_bsp
require recipes-kernel/linux/linux-yocto.inc

LINUX_VERSION ?= "4.9.140"
PV = "${LINUX_VERSION}+git${SRCPV}"
FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}-${@bb.parse.BBHandler.vars_from_file(d.getVar('FILE', False),d)[1]}:"

LINUX_VERSION_EXTENSION ?= "-l4t-r${L4T_VERSION}"
SCMVERSION ??= "y"

SRCBRANCH = "patches${LINUX_VERSION_EXTENSION}"
SRCREV = "e3c8d3e9030a4a9dbe6171355a062ebfad28dfde"
KBRANCH = "${SRCBRANCH}"
SRC_REPO = "github.com/madisongh/linux-tegra-4.9"
KERNEL_REPO = "${SRC_REPO}"
SRC_URI = "git://${KERNEL_REPO};name=machine;branch=${KBRANCH} \
	   ${@'file://localversion_auto.cfg' if d.getVar('SCMVERSION') == 'y' else ''} \
"

KBUILD_DEFCONFIG = "tegra_defconfig"
KCONFIG_MODE = "--alldefconfig"

set_scmversion() {
    if [ "${SCMVERSION}" = "y" -a -d "${S}/.git" ]; then
        head=$(git --git-dir=${S}/.git rev-parse --verify --short HEAD 2>/dev/null || true)
        [ -z "$head" ] || echo "+g$head" > ${S}/.scmversion
    fi
}
do_kernel_checkout[postfuncs] += "set_scmversion"

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
	    ${STAGING_BINDIR_NATIVE}/tegra186-flash/mkbootimg \
				    --kernel $deployDir/${initramfs_base_name}.bin \
				    --ramdisk ${WORKDIR}/initrd \
				    --output $deployDir/${initramfs_base_name}.cboot
	    chmod 0644 $deployDir/${initramfs_base_name}.cboot
	    ln -sf ${initramfs_base_name}.cboot $deployDir/${initramfs_symlink_name}.cboot
	done
    fi
}
do_deploy_append_tegra186() {
    bootimg_from_bundled_initramfs
}
do_deploy_append_tegra194() {
    bootimg_from_bundled_initramfs
}

EXTRADEPLOYDEPS = ""
EXTRADEPLOYDEPS_tegra186 = "tegra186-flashtools-native:do_populate_sysroot"
EXTRADEPLOYDEPS_tegra194 = "tegra186-flashtools-native:do_populate_sysroot"
do_deploy[depends] += "${EXTRADEPLOYDEPS}"

COMPATIBLE_MACHINE = "(tegra)"

RDEPENDS_${KERNEL_PACKAGE_NAME}-base = "${@'' if d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('cboot') else '${KERNEL_PACKAGE_NAME}-image'}"
