SECTION = "kernel"
SUMMARY = "Linux for Tegra kernel recipe"
DESCRIPTION = "Linux kernel from sources provided by Nvidia for Tegra processors."
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

require recipes-kernel/linux/linux-yocto.inc

LINUX_VERSION ?= "4.9.140"
PV = "${LINUX_VERSION}+git${SRCPV}"
FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}-${@bb.parse.BBHandler.vars_from_file(d.getVar('FILE', False),d)[1]}:"
EXTRA_OEMAKE += 'LIBGCC=""'

LINUX_VERSION_EXTENSION ?= "-l4t-r32.3.1"

KBRANCH = "patches${LINUX_VERSION_EXTENSION}"
SRCREV = "47e7e1cb0b492487faa6258a4f3efe91676568b7"
KERNEL_REPO = "github.com/madisongh/linux-tegra-4.9"
SRC_URI = "git://${KERNEL_REPO};branch=${KBRANCH} \
	   file://defconfig \
"
S = "${WORKDIR}/git"

COMPATIBLE_MACHINE = "(tegra)"

RDEPENDS_${KERNEL_PACKAGE_NAME}-base = "${@'' if d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('cboot') else '${KERNEL_PACKAGE_NAME}-image'}"
