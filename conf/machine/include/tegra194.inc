# Settings common to all Xavier (tegra194) SoCs

SOC_FAMILY = "tegra194"

require conf/machine/include/soc-family.inc
DEFAULTTUNE ?= "armv8a-crc"
require conf/machine/include/arm/arch-armv8-2a.inc

INITRAMFS_IMAGE ?= "tegra-minimal-initramfs"
INITRAMFS_IMAGE_BUNDLE ?= "1"
IMAGE_UBOOT ?= ""
TEGRA_INITRAMFS_FSTYPES ?= "${@'' if d.getVar('INITRAMFS_IMAGE_BUNDLE') == '1' else ' cpio.gz.cboot cpio.gz.cboot.bup-payload'}"

TEGRA_AUDIO_DEVICE ?= "tegrahdat194ref"

SERIAL_CONSOLES ?= "115200;ttyTCU0"

KERNEL_MODULE_AUTOLOAD ?= "nvgpu"

NVIDIA_CHIP = "0x19"

TEGRA_BOARDID ?= "2888"
TEGRA_FAB ?= "400"
TEGRA_BOARDSKU ?= "0001"
TEGRA_BOARDREV ?= "H.0"
TEGRA_CHIPREV ?= "2"
TEGRA_MINRATCHET_CONFIG ?= "tegra194-mb1-bct-ratchet-p2888-0000-p2822-0000.cfg"
TEGRA_SIGNING_ENV ?= "CHIPREV=${TEGRA_CHIPREV} BOARDID=${TEGRA_BOARDID} FAB=${TEGRA_FAB} BOARDSKU=${TEGRA_BOARDSKU} BOARDREV=${TEGRA_BOARDREV} fuselevel=fuselevel_production"

TEGRA_CUDA_ARCHITECTURE = "72"

PREFERRED_PROVIDER_virtual/bootloader ?= "cboot-t19x"
PREFERRED_PROVIDER_tos-trusty ?= "tos-prebuilt"

UBOOT_EXTLINUX ?= ""
UBOOT_EXTLINUX_KERNEL_IMAGE ?= "/boot/${KERNEL_IMAGETYPE}"
UBOOT_EXTLINUX_INITRD ?= "${@'/boot/initrd' if d.getVar('INITRAMFS_IMAGE') != '' and d.getVar('INITRAMFS_IMAGE_BUNDLE') != '1' else ''}"
# This is inserted into UBOOT_EXTLINUX_ROOT in tegra-common.inc
KERNEL_ROOTSPEC ?= "rootwait"
UBOOT_EXTLINUX_KERNEL_ARGS ?= ""

require conf/machine/include/tegra-common.inc

L4T_BSP_NAME = "T186"
L4T_BSP_PREFIX = "Jetson"
L4T_DEB_SOCNAME = "t194"

NVPMODEL ?= "nvpmodel_t194"

PARTITION_LAYOUT_EXTERNAL ?= "flash_l4t_nvme.xml"
