inherit kernel

require recipes-kernel/linux/linux-dtb.inc

EXTRA_OEMAKE += 'LIBGCC=""'

LOCALVERSION = "-${L4T_VERSION}"

KERNEL_ROOTSPEC ?= "root=/dev/mmcblk\${devnum}p1 ro rootwait"

do_install[postfuncs] += "generate_extlinux_conf"

FILES_kernel-image += "/${KERNEL_IMAGEDEST}/extlinux"
