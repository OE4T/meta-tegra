SECTION = "kernel"
SUMMARY = "Linux for Tegra kernel recipe"
DESCRIPTION = "Linux kernel from sources provided by Nvidia for Tegra processors."
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

inherit l4t_bsp
require recipes-kernel/linux/linux-yocto.inc

DEPENDS_remove = "kern-tools-native"
DEPENDS_append = " kern-tools-tegra-native"

LINUX_VERSION ?= "4.9.140"
PV = "${LINUX_VERSION}+git${SRCPV}"
FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}-${@bb.parse.BBHandler.vars_from_file(d.getVar('FILE', False),d)[1]}:"

LINUX_VERSION_EXTENSION ?= "-l4t-r${@'.'.join(d.getVar('L4T_VERSION').split('.')[:2])}"
SCMVERSION ??= "y"

SRCBRANCH = "oe4t-patches${LINUX_VERSION_EXTENSION}"
SRCREV = "a28ca7b3779d65b032aecf80b58be4d8458a2ded"
KBRANCH = "${SRCBRANCH}"
SRC_REPO = "github.com/OE4T/linux-tegra-4.9;protocol=https"
KERNEL_REPO = "${SRC_REPO}"
SRC_URI = "git://${KERNEL_REPO};name=machine;branch=${KBRANCH} \
           ${@'file://localversion_auto.cfg' if d.getVar('SCMVERSION') == 'y' else ''} \
           ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'file://systemd.cfg', '', d)} \
"

PATH_prepend = "${STAGING_BINDIR_NATIVE}/kern-tools-tegra:"

KBUILD_DEFCONFIG = "tegra_defconfig"
KCONFIG_MODE = "--alldefconfig"

set_scmversion() {
    if [ "${SCMVERSION}" = "y" -a -d "${S}/.git" ]; then
        head=$(git --git-dir=${S}/.git rev-parse --verify --short HEAD 2>/dev/null || true)
        [ -z "$head" ] || echo "+g$head" > ${S}/.scmversion
    fi
}
do_kernel_checkout[postfuncs] += "set_scmversion"

python do_kernel_configcheck_prepend() {
    os.environ['KERNEL_OVERLAYS'] = d.expand("${S}/nvidia ${S}/nvidia/nvgpu")
}

require linux-tegra-bootimg.inc
