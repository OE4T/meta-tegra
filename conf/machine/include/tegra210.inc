# Settings common to all Tegra X1 (tegra210) SoCs

SOC_FAMILY = "tegra210"

KERNEL_IMAGETYPE ?= "Image"
KERNEL_IMAGETYPES ?= "${KERNEL_IMAGETYPE}"

require conf/machine/include/soc-family.inc
require conf/machine/include/arm/arch-armv8a.inc

TEGRA_ESSENTIAL_EXTRA_RDEPENDS = "${@'' if d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('cboot') else 'kernel-image u-boot-extlinux'}"

INITRAMFS_IMAGE ?= "tegra-minimal-initramfs"
IMAGE_UBOOT ?= "${@'' if d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('cboot') else 'u-boot'}"
TEGRA_INITRAMFS_FSTYPES ?= "${@'' if not (d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('cboot') and d.getVar('INITRAMFS_IMAGE_BUNDLE') != '1') else ' cpio.gz.cboot'}"

TEGRA_AUDIO_DEVICE ?= "tegrahda"

SERIAL_CONSOLES ?= "115200;ttyS0"

KERNEL_MODULE_AUTOLOAD = "nvgpu"

KERNEL_ARGS ?= "sdhci_tegra.en_boot_part_access=1"

NVIDIA_CHIP = "0x21"

TEGRA_SIGNING_ENV ?= "BOARDID=${TEGRA_BOARDID} FAB=${TEGRA_FAB} BOARDSKU=${TEGRA_BOARDSKU} BOARDREV=${TEGRA_BOARDREV}"

TEGRA_CUDA_ARCHITECTURE ?= "53"

require conf/machine/include/tegra-common.inc

L4T_BSP_NAME = "T210"
L4T_BSP_PREFIX = "Jetson-210"
L4T_DEB_SOCNAME = "t210"
L4T_BSP_ARCH = "tegra210"
L4T_BSP_PKGARCH = "${SOC_FAMILY_PKGARCH}"

NVPMODEL ?= "nvpmodel_t210"
TEGRA_BOARDID ?= "2180"
TEGRA_FAB ?= "100"
TEGRA_BOARDSKU ?= ""
TEGRA_BOARDREV ?= ""
TEGRA_CHIPREV ?= "0"

PARTITION_LAYOUT_EXTERNAL ?= "external_layout.xml"
BOOT_PARTITIONS_ON_EMMC = "1"
