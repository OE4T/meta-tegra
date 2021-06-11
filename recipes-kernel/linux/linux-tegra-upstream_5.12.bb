SECTION = "kernel"
DESCRIPTION = "Linux upstream kernel recipe for Tegra processors"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

KBRANCH = "linux-5.12.y"

require recipes-kernel/linux/linux-yocto.inc

LINUX_VERSION ?= "5.12.8"
PV = "${LINUX_VERSION}+git${SRCPV}"

SRCREV ?= "cfb3ea79045ad3c7bcaa0036b5a66609ccdadffe"

SRC_URI = "git://git.kernel.org/pub/scm/linux/kernel/git/stable/linux.git;branch=${KBRANCH};nocheckout=1"
SRC_URI += "file://tegra.cfg"

KBUILD_DEFCONFIG = "defconfig"
KCONFIG_MODE = "--alldefconfig"
KCONF_BSP_AUDIT_LEVEL = "1"

COMPATIBLE_MACHINE = "(tegra)"

require linux-tegra-bootimg.inc
