SECTION = "kernel"
SUMMARY = "Linux for Tegra kernel recipe"
DESCRIPTION = "Linux kernel from sources provided by Nvidia for Tegra processors."
LICENSE = "GPL-2.0-only"
LIC_FILES_CHKSUM = "file://COPYING;md5=6bc538ed5bd9a7fc9398086aedcd7e46"

inherit l4t_bsp python3native
require recipes-kernel/linux/linux-yocto.inc
require tegra-kernel.inc

KERNEL_DISABLE_FW_USER_HELPER ?= "y"

LINUX_VERSION ?= "6.8.12"
PV = "${LINUX_VERSION}"

FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}-${@bb.parse.vars_from_file(d.getVar('FILE', False),d)[1]}:"

LINUX_VERSION_EXTENSION ?= "-l4t-r${@'.'.join(d.getVar('L4T_VERSION').split('.')[0:3])}-1009.9"
SCMVERSION ??= "y"

SRCBRANCH = "l4t/l4t-r38.4-Ubuntu-nvidia-tegra-pvw-6.8.0-1009.9"
SRCREV = "ad9393b41b82eafd8fd39e5d3d160c754d4249e5"
KBRANCH = "${SRCBRANCH}"
SRC_REPO = "gitlab.com/nvidia/nv-tegra/3rdparty/canonical/linux-noble.git;protocol=https"
KERNEL_REPO = "${SRC_REPO}"
SRC_URI = "git://${KERNEL_REPO};name=machine;branch=${KBRANCH} \
    file://0001-tty-vt-conmakehash-remove-non-portable-code-printing.patch \
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
