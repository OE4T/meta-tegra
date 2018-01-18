SECTION = "kernel"
SUMMARY = "Linux for Tegra kernel recipe"
DESCRIPTION = "Linux kernel from sources provided by Nvidia for Tegra processors."
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

inherit kernel

PV .= "+git${SRCPV}"
FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}-${@bb.parse.BBHandler.vars_from_file(d.getVar('FILE', False),d)[1]}:"
EXTRA_OEMAKE += 'LIBGCC=""'

L4T_VERSION_tegra124 = "l4t-r21.6"
LOCALVERSION = "-${L4T_VERSION}"

SRCBRANCH = "patches-${L4T_VERSION}"
SRCREV_tegra124 = "d5cd2708948fe433d7adf56b0b39dd6d50f3b99e"
KERNEL_REPO = "github.com/madisongh/linux-tegra.git"
SRC_URI = "git://${KERNEL_REPO};branch=${SRCBRANCH} \
	   file://defconfig \
"
S = "${WORKDIR}/git"

export KCFLAGS = ""
# Use Wno-option instead of Wno-error=option because the latter breaks on older GCC such as 4.9 and 4.8
KCFLAGS_tegra124 = "-Wno-unused-const-variable -Wno-misleading-indentation \
                    -Wno-switch-unreachable -Wno-parentheses -Wno-maybe-uninitialized \
                    -Wno-format-truncation -Wno-format-overflow -Wno-int-in-bool-context \
                    "


KERNEL_ROOTSPEC ?= "root=/dev/mmcblk\${devnum}p1 ro rootwait"

do_configure_prepend() {
    sed -e's,^CONFIG_LOCALVERSION=.*$,CONFIG_LOCALVERSION="${LOCALVERSION}",' < ${WORKDIR}/defconfig > ${B}/.config
    head=`git --git-dir=${S}/.git rev-parse --verify --short HEAD 2> /dev/null`
    printf "%s%s" "+g" $head > ${S}/.scmversion
}

KERNEL_ARGS_tegra124 = "console=ttyS0,115200n8 console=tty1 no_console_suspend=1 lp0_vec=2064@0xf46ff000 mem=2015M@2048M memtype=255 ddr_die=2048M@2048M section=256M pmuboard=0x0177:0x0000:0x02:0x43:0x00 tsec=32M@3913M otf_key=c75e5bb91eb3bd947560357b64422f85 usbcore.old_scheme_first=1 core_edp_mv=1150 core_edp_ma=4000 tegraid=40.1.1.0.0 debug_uartport=lsport,3 power_supply=Adapter audio_codec=rt5640 modem_id=0 android.kerneltype=normal fbcon=map:1 commchip_id=0 usb_port_owner_info=0 lane_owner_info=6 emc_max_dvfs=0 touch_id=0@0 board_info=0x0177:0x0000:0x02:0x43:0x00 rw rootwait gpt"

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
      APPEND ${KERNEL_ARGS} ${KERNEL_ROOTSPEC}
EOF
        i=$(expr $i \+ 1)
    done
}

do_install[postfuncs] += "generate_extlinux_conf"

FILES_kernel-image += "/${KERNEL_IMAGEDEST}/extlinux"

COMPATIBLE_MACHINE = "(tegra124)"
