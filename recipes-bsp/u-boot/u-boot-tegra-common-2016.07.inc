# This file is shared with fw-utils and u-boot builds
LICENSE = "GPLv2+"
DESCRIPTION = "U-Boot for Nvidia Tegra platforms, based on Nvidia sources"
COMPATIBLE_MACHINE = "(tegra186|tegra210)"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=a2c678cfd4a4d97135585cad908541c6"
HOMEPAGE = "https://github.com/OE4T/u-boot-tegra"

UBOOT_TEGRA_REPO ?= "github.com/OE4T/u-boot-tegra.git"
SRCBRANCH ?= "patches-l4t-r32.3.1-v2016.07"
SRC_URI = "git://${UBOOT_TEGRA_REPO};branch=${SRCBRANCH}"
SRCREV = "a8c48a2aadb115ef9eb01fa5ab496a24fce65a25"
PV .= "+git${SRCPV}"

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
