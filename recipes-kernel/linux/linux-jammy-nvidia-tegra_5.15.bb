SECTION = "kernel"
SUMMARY = "Linux for Tegra kernel recipe"
DESCRIPTION = "Linux kernel from sources provided by Nvidia for Tegra processors."
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

inherit l4t_bsp python3native
require recipes-kernel/linux/linux-yocto.inc
require tegra-kernel.inc

KERNEL_DISABLE_FW_USER_HELPER ?= "y"

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
