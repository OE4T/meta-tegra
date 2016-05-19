SECTION = "kernel"
SUMMARY = "Linux for Tegra kernel recipe"
DESCRIPTION = "Linux kernel from sources provided by Nvidia for Tegra processors."
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

inherit kernel

require recipes-kernel/linux/linux-dtb.inc

PV .= "+git${SRCPV}"
FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}-${@bb.parse.BBHandler.vars_from_file(d.getVar('FILE', False),d)[1]}:"
DEPENDS_append_aarch64 = " lib32-gcc-cross-arm lib32-gcc-runtime lib32-binutils-cross-arm"
CROSS32TC = ""
CROSS32TC_aarch64 = "${TUNE_ARCH_32}${TARGET_VENDOR_virtclass-multilib-lib32}-${TARGET_OS}-gnu${ABIEXTENSION_32}"
PATH_prepend_aarch64 = "${STAGING_BINDIR_NATIVE}/${CROSS32TC}:"
EXTRA_OEMAKE_append_aarch64 = ' CROSS32CC="${CROSS32TC}-gcc -march=armv7-a -mfloat-abi=hard" CROSS32LD="${CROSS32TC}-ld.bfd"'
EXTRA_OEMAKE += 'LIBGCC=""'

L4T_VERSION = "l4t-r24.1"
LOCALVERSION = "-${L4T_VERSION}"

SRCBRANCH = "patches-${L4T_VERSION}"
SRCREV = "34ac32e0e19e677ec7404f02a582c818b40812cd"
KERNEL_REPO = "github.com/madisongh/linux-tegra.git"
SRC_URI = "git://${KERNEL_REPO};branch=${SRCBRANCH} \
	   file://defconfig \
"
S = "${WORKDIR}/git"

do_configure_prepend() {
    sed -e's,^CONFIG_LOCALVERSION=.*$,CONFIG_LOCALVERSION="${LOCALVERSION}",' < ${WORKDIR}/defconfig > ${B}/.config
    head=`git --git-dir=${S}/.git rev-parse --verify --short HEAD 2> /dev/null`
    printf "%s%s" "+g" $head > ${S}/.scmversion
}

do_install_append() {
    install -d ${D}/${KERNEL_IMAGEDEST}/extlinux
    rm -f ${D}/${KERNEL_IMAGEDEST}/extlinux/extlinux.conf
    cat >${D}/${KERNEL_IMAGEDEST}/extlinux/extlinux.conf << EOF
DEFAULT primary
TIMEOUT 30
MENU TITLE Boot Options
LABEL primary
      MENU LABEL primary ${KERNEL_IMAGETYPE}-${KERNEL_VERSION}
      LINUX /${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}-${KERNEL_VERSION}
      FDT /${KERNEL_IMAGEDEST}/devicetree-${KERNEL_IMAGETYPE}-${KERNEL_DEVICETREE}
      APPEND console=ttyS0,115200n8 ddr_die=2048M@2048M ddr_die=2048M@4096M section=256M memtype=0 vpr_resize usb_port_owner_info=0 lane_owner_info=0 emc_max_dvfs=0 touch_id=0@63 video=tegrafb no_console_suspend=1 debug_uartport=lsport,0 earlyprintk=uart8250-32bit,0x70006000 maxcpus=4 usbcore.old_scheme_first=1 lp0_vec=\${lp0_vec} nvdumper_reserved=\${nvdumper_reserved} core_edp_mv=1125 core_edp_ma=4000 gpt root=/dev/mmcblk1p1 ro rootwait
EOF
}

FILES_kernel-image += "/${KERNEL_IMAGEDEST}/extlinux"

COMPATIBLE_MACHINE = "(jetson-tx1)"
