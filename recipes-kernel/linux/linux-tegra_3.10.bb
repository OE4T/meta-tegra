SECTION = "kernel"
SUMMARY = "Linux for Tegra kernel recipe"
DESCRIPTION = "Linux kernel from sources provided by Nvidia for Tegra processors."
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

inherit kernel

PV .= "+git${SRCPV}"
FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}-${@bb.parse.BBHandler.vars_from_file(d.getVar('FILE', False),d)[1]}:"
EXTRA_OEMAKE += 'LIBGCC=""'

L4T_VERSION = "l4t-r21.7"
LOCALVERSION = "-${L4T_VERSION}"

SRCBRANCH = "patches-${L4T_VERSION}"
SRCREV = "866a49b773e64cc089d5ad2d06833f0e005caacd"
KERNEL_REPO = "github.com/madisongh/linux-tegra.git"
SRC_URI = "git://${KERNEL_REPO};branch=${SRCBRANCH} \
	   file://defconfig \
"
S = "${WORKDIR}/git"

# Use Wno-option instead of Wno-error=option because the latter breaks on older GCC such as 4.9 and 4.8
export KCFLAGS = "-Wno-unused-const-variable -Wno-misleading-indentation \
                  -Wno-switch-unreachable -Wno-parentheses -Wno-maybe-uninitialized \
                  -Wno-format-truncation -Wno-format-overflow -Wno-int-in-bool-context \
                  "


KERNEL_ROOTSPEC ?= "root=/dev/mmcblk\${devnum}p1 ro rootwait"

do_configure_prepend() {
    sed -e's,^CONFIG_LOCALVERSION=.*$,CONFIG_LOCALVERSION="${LOCALVERSION}",' < ${WORKDIR}/defconfig > ${B}/.config
    head=`git --git-dir=${S}/.git rev-parse --verify --short HEAD 2> /dev/null`
    printf "%s%s" "+g" $head > ${S}/.scmversion
}

COMPATIBLE_MACHINE = "(tegra124)"
