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
PV = "${LINUX_VERSION}+git${SRCPV}"
FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}-${@bb.parse.vars_from_file(d.getVar('FILE', False),d)[1]}:"

LINUX_VERSION_EXTENSION ?= "-l4t-r${@'.'.join(d.getVar('L4T_VERSION').split('.')[0:3])}"
SCMVERSION ??= "y"

TEGRA_SRC_SUBARCHIVE = "\
    Linux_for_Tegra/source/kernel_src.tbz2 \
"
TEGRA_SRC_SUBARCHIVE_OPTS = "-C ${UNPACKDIR}/${BPN}"
require recipes-bsp/tegra-sources/tegra-sources-38.4.0.inc

do_unpack[depends] += "tegra-binaries:do_preconfigure"
do_unpack[dirs] += "${UNPACKDIR}/${BPN}"

SRC_URI += " \
    ${@'file://localversion_auto.cfg' if d.getVar('SCMVERSION') == 'y' else ''} \
    ${@'file://disable-fw-user-helper.cfg' if d.getVar('KERNEL_DISABLE_FW_USER_HELPER') == 'y' else ''} \
    ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'file://systemd.cfg', '', d)} \
    file://disable-module-signing.cfg \
"

KBUILD_DEFCONFIG = "defconfig"
KCONFIG_MODE = "--alldefconfig"

# Copy the Linux kernel sources to the right location
prepare_source() {
    cp -r ${UNPACKDIR}/${BPN}/kernel/kernel-noble/* ${S}/
}
do_unpack[postfuncs] += "prepare_source"

set_scmversion() {
    if [ "${SCMVERSION}" = "y" -a -d "${S}/.git" ]; then
        head=$(git --git-dir=${S}/.git rev-parse --verify --short HEAD 2>/dev/null || true)
        [ -z "$head" ] || echo "+g$head" > ${S}/.scmversion
    fi
}
do_kernel_checkout[postfuncs] += "set_scmversion"

INSANE_SKIP:${PN}-src = "buildpaths"
