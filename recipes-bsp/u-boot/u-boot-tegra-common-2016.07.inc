LICENSE = "GPLv2+"
DESCRIPTION = "U-Boot for Nvidia Tegra186 platforms, based on Nvidia sources"
COMPATIBLE_MACHINE = "(tegra186|tegra210)"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=a2c678cfd4a4d97135585cad908541c6"

UBOOT_TEGRA_REPO ?= "github.com/madisongh/u-boot-tegra.git"
SRCBRANCH ?= "patches-l4t-r28.2"
SRC_URI = "git://${UBOOT_TEGRA_REPO};branch=${SRCBRANCH}"
SRCREV = "8a044b187228e60b454529e72c0610edd9d845bf"
PV .= "+git${SRCPV}"

S = "${WORKDIR}/git"

KERNEL_ROOTSPEC ??= ""
KERNEL_ARGS ??= ""

def uboot_var(name):
    return '${' + name + '}'

UBOOT_EXTLINUX = "1"
UBOOT_EXTLINUX_LABELS = "primary"
UBOOT_EXTLINUX_DEFAULT_LABEL = "primary"
UBOOT_EXTLINUX_KERNEL_IMAGE_primary = "../${KERNEL_IMAGETYPE}"
UBOOT_EXTLINUX_FDTDIR_primary = ""
UBOOT_EXTLINUX_FDT_primary = ""
UBOOT_EXTLINUX_ROOT_primary = "${@uboot_var('cbootargs')} ${KERNEL_ROOTSPEC}"
# Console setting comes from ${cbootargs}
UBOOT_EXTLINUX_CONSOLE = ""
UBOOT_EXTLINUX_KERNEL_ARGS_primary = "${KERNEL_ARGS}"
UBOOT_EXTLINUX_INITRD_primary = "${@'../initrd' if d.getVar('INITRAMFS_IMAGE') != '' and d.getVar('TEGRA_INITRAMFS_INITRD') == '1' else ''}"

do_install_append() {
	install -m 0644 ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE} ${D}/boot/
	if [ -n "${INITRAMFS_IMAGE}" -a "${TEGRA_INITRAMFS_INITRD}" = "1" ]; then
		install -m 0644 ${DEPLOY_DIR_IMAGE}/${INITRAMFS_IMAGE}-${MACHINE}.cpio.gz ${D}/boot/initrd
	fi
}
do_install[depends] += "${@'${INITRAMFS_IMAGE}:do_image_complete' if d.getVar('INITRAMFS_IMAGE') != '' and d.getVar('TEGRA_INITRAMFS_INITRD') == '1' else ''}"
