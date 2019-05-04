SECTION = "kernel"
SUMMARY = "Linux for Tegra kernel recipe"
DESCRIPTION = "Linux kernel from sources provided by Nvidia for Tegra processors."
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

inherit kernel

PV .= "+git${SRCPV}"
FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}-${@bb.parse.BBHandler.vars_from_file(d.getVar('FILE', False),d)[1]}:"
EXTRA_OEMAKE += 'LIBGCC=""'

L4T_VERSION = "l4t-r32.1"
SCMVERSION ??= "y"
export LOCALVERSION = ""

SRCBRANCH = "patches-${L4T_VERSION}"
SRCREV = "3c02a65d917016f5301422d76c255638c845d774"
KERNEL_REPO = "github.com/madisongh/linux-tegra-4.9"
SRC_URI = "git://${KERNEL_REPO};branch=${SRCBRANCH} \
	   file://defconfig \
"
S = "${WORKDIR}/git"

do_configure_prepend() {
    localversion="-${L4T_VERSION}"
    if [ "${SCMVERSION}" = "y" ]; then
	head=`git --git-dir=${S}/.git rev-parse --verify --short HEAD 2> /dev/null`
        [ -z "$head" ] || localversion="${localversion}+g${head}"
    fi
    sed -e"s,^CONFIG_LOCALVERSION=.*$,CONFIG_LOCALVERSION=\"${localversion}\"," \
	< ${WORKDIR}/defconfig > ${B}/.config
}

COMPATIBLE_MACHINE = "(tegra)"
COMPATIBLE_MACHINE_tegra124 = "(-)"

RDEPENDS_${KERNEL_PACKAGE_NAME}-base = "${@'' if d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('cboot') else '${KERNEL_PACKAGE_NAME}-image'}"
