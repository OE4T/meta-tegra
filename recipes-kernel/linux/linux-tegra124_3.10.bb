SECTION = "kernel"
SUMMARY = "Linux for Tegra kernel recipe"
DESCRIPTION = "Linux kernel from sources provided by Nvidia for Tegra processors."
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

require linux-tegra.inc

SRC_URI += " \
  http://developer.download.nvidia.com/embedded/L4T/r21_Release_v5.0/source/kernel_src.tbz2 \
  file://defconfig \
"

SRC_URI[md5sum] = "266f2159d8e4f301ac879fbc5615352d"
SRC_URI[sha256sum] = "4b03c0937087e439fe2b93c537565116057e9289ee97240a4ce7895decb6a769"

S = "${WORKDIR}/kernel"

L4T_VERSION = "l4t-r21.5"

do_configure_prepend() {
    sed -e's,^CONFIG_LOCALVERSION=.*$,CONFIG_LOCALVERSION="${LOCALVERSION}",' < ${WORKDIR}/defconfig > ${B}/.config
    printf "%s" "-${L4T_VERSION}" > ${S}/.scmversion
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
      APPEND console=ttyS0,115200n8 console=tty1 no_console_suspend=1 lp0_vec=2064@0xf46ff000 mem=2015M@2048M memtype=255 ddr_die=2048M@2048M section=256M pmuboard=0x0177:0x0000:0x02:0x43:0x00 tsec=32M@3913M otf_key=c75e5bb91eb3bd947560357b64422f85 usbcore.old_scheme_first=1 core_edp_mv=1150 core_edp_ma=4000 tegraid=40.1.1.0.0 debug_uartport=lsport,3 power_supply=Adapter audio_codec=rt5640 modem_id=0 android.kerneltype=normal fbcon=map:1 commchip_id=0 usb_port_owner_info=0 lane_owner_info=6 emc_max_dvfs=0 touch_id=0@0 board_info=0x0177:0x0000:0x02:0x43:0x00 rw rootwait gpt ${KERNEL_ROOTSPEC}
EOF
        i=$(expr $i \+ 1)
    done
}

COMPATIBLE_MACHINE = "(tegra124)"
