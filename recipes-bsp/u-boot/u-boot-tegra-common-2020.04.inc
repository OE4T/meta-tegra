# This file is shared with fw-utils and u-boot builds
LICENSE = "GPLv2+"
DESCRIPTION = "U-Boot for Nvidia Tegra platforms, based on Nvidia sources"
COMPATIBLE_MACHINE = "(tegra186|tegra210)"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=30503fd321432fc713238f582193b78e"

SRC_REPO ?= "github.com/OE4T/u-boot-tegra.git;protocol=https"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCBRANCH ?= "patches-v2020.04-l4t-r32.4.3"
SRCREV = "f26880956be0147dd96e716c2ea034e223603776"

S = "${WORKDIR}/git"

KERNEL_ROOTSPEC ??= ""
KERNEL_ARGS ??= ""

def uboot_var(name):
    return '${' + name + '}'

UBOOT_EXTLINUX = "1"
UBOOT_EXTLINUX_LABELS = "primary"
UBOOT_EXTLINUX_KERNEL_IMAGE_primary = "../${KERNEL_IMAGETYPE}"
UBOOT_EXTLINUX_FDTDIR_primary = ""
UBOOT_EXTLINUX_FDT_primary = ""
UBOOT_EXTLINUX_ROOT_primary = "${@uboot_var('cbootargs')} ${KERNEL_ROOTSPEC}"
# Console setting comes from ${cbootargs}
UBOOT_EXTLINUX_CONSOLE = ""
UBOOT_EXTLINUX_KERNEL_ARGS_primary = "${@uboot_var('bootargs')}"
UBOOT_EXTLINUX_INITRD_primary = "${@'../initrd' if d.getVar('INITRAMFS_IMAGE') != '' and d.getVar('TEGRA_INITRAMFS_INITRD') == '1' else ''}"
