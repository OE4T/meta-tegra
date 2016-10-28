SECTION = "kernel"
SUMMARY = "Linux for Tegra kernel recipe"
DESCRIPTION = "Linux kernel from sources provided by Nvidia for Tegra processors."
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

require linux-tegra.inc

L4T_VERSION = "l4t-r24.2"

PV .= "+git${SRCPV}"
SRCBRANCH = "patches-${L4T_VERSION}"
SRCREV = "271164dd7765d467af54293409fd9a3fb449942e"
KERNEL_REPO = "github.com/madisongh/linux-tegra.git"
SRC_URI += "git://${KERNEL_REPO};branch=${SRCBRANCH}"
SRC_URI += "file://defconfig"

S = "${WORKDIR}/git"

do_configure_prepend() {
    sed -e's,^CONFIG_LOCALVERSION=.*$,CONFIG_LOCALVERSION="${LOCALVERSION}",' < ${WORKDIR}/defconfig > ${B}/.config
    head=`git --git-dir=${S}/.git rev-parse --verify --short HEAD 2> /dev/null`
    printf "%s%s" "+g" $head > ${S}/.scmversion
}

generate_extlinux_conf() {
    install -d ${D}/${KERNEL_IMAGEDEST}/extlinux
    rm -f ${D}/${KERNEL_IMAGEDEST}/extlinux/extlinux.conf
    cat >${D}/${KERNEL_IMAGEDEST}/extlinux/extlinux.conf << EOF
DEFAULT primary-1
TIMEOUT 30
MENU TITLE Boot Options
EOF
    i=1
    for fdt in ${KERNEL_DEVICETREE}; do
        cat >>${D}/${KERNEL_IMAGEDEST}/extlinux/extlinux.conf << EOF
LABEL primary-$i
      MENU LABEL primary-$i ${KERNEL_IMAGETYPE}-${KERNEL_VERSION} $fdt
      LINUX /${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}-${KERNEL_VERSION}
      FDT /${KERNEL_IMAGEDEST}/devicetree-${KERNEL_IMAGETYPE}-$fdt
      APPEND fbcon=map:0 console=tty0 console=ttyS0,115200n8 ddr_die=2048M@2048M ddr_die=2048M@4096M section=256M memtype=0 vpr_resize usb_port_owner_info=0 lane_owner_info=0 emc_max_dvfs=0 touch_id=0@63 video=tegrafb no_console_suspend=1 debug_uartport=lsport,0 earlyprintk=uart8250-32bit,0x70006000 maxcpus=4 usbcore.old_scheme_first=1 lp0_vec=\${lp0_vec} nvdumper_reserved=\${nvdumper_reserved} core_edp_mv=1125 core_edp_ma=4000 gpt ${KERNEL_ROOTSPEC}
EOF
        i=$(expr $i \+ 1)
    done
}

COMPATIBLE_MACHINE = "(tegra210)"
