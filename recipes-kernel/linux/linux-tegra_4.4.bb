SECTION = "kernel"
SUMMARY = "Linux for Tegra kernel recipe"
DESCRIPTION = "Linux kernel from sources provided by Nvidia for Tegra processors."
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

inherit kernel

require recipes-kernel/linux/linux-dtb.inc

PV .= "+git${SRCPV}"
FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}-${@bb.parse.BBHandler.vars_from_file(d.getVar('FILE', False),d)[1]}:"
EXTRA_OEMAKE += 'LIBGCC=""'

L4T_VERSION_tegra186 = "l4t-r27.1"
LOCALVERSION = "-${L4T_VERSION}"

SRCBRANCH = "patches-${L4T_VERSION}"
SRCREV = "${AUTOREV}"
KERNEL_REPO = "github.com/madisongh/linux-tegra.git"
SRC_URI = "git://${KERNEL_REPO};branch=${SRCBRANCH} \
	   file://defconfig \
"
S = "${WORKDIR}/git"

KERNEL_ROOTSPEC ?= "root=/dev/mmcblk\${devnum}p1 ro rootwait"

do_configure_prepend() {
    sed -e's,^CONFIG_LOCALVERSION=.*$,CONFIG_LOCALVERSION="${LOCALVERSION}",' < ${WORKDIR}/defconfig > ${B}/.config
    head=`git --git-dir=${S}/.git rev-parse --verify --short HEAD 2> /dev/null`
    printf "%s%s" "+g" $head > ${S}/.scmversion
}

KERNEL_ARGS_tegra186 = "fbcon=map:0 console=tty0 console=ttyS0,115200n8 memtype=0 video=tegrafb no_console_suspend=1 earlycon=uart8250,mmio32,0x03100000 gpt tegraid=18.1.2.0.0 tegra_keep_boot_clocks maxcpus=6 vpr_resize"

generate_extlinux_conf() {
    install -d ${D}/${KERNEL_IMAGEDEST}/extlinux
    rm -f ${D}/${KERNEL_IMAGEDEST}/extlinux/extlinux.conf
    cat >${D}/${KERNEL_IMAGEDEST}/extlinux/extlinux.conf << EOF
DEFAULT primary-1
TIMEOUT 30
MENU TITLE Boot Options
EOF
    i=1
    for f in ${KERNEL_DEVICETREE}; do
        fdt=$(basename $f)
        cat >>${D}/${KERNEL_IMAGEDEST}/extlinux/extlinux.conf << EOF
LABEL primary-$i
      MENU LABEL primary-$i ${KERNEL_IMAGETYPE}-${KERNEL_VERSION} $fdt
      LINUX /${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}-${KERNEL_VERSION}
      FDT /${KERNEL_IMAGEDEST}/devicetree-${KERNEL_IMAGETYPE}-$fdt
      APPEND ${KERNEL_ARGS} ${KERNEL_ROOTSPEC}
EOF
        i=$(expr $i \+ 1)
    done
}

do_install[postfuncs] += "generate_extlinux_conf"

FILES_kernel-image += "/${KERNEL_IMAGEDEST}/extlinux"

COMPATIBLE_MACHINE = "(tegra186)"
