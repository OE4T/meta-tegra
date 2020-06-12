# Settings common to all Tegra X1 (tegra210) SoCs

SOC_FAMILY = "tegra210"

require conf/machine/include/soc-family.inc
require conf/machine/include/arm/arch-armv8a.inc

KERNEL_IMAGETYPE = "Image"

TEGRA_ESSENTIAL_EXTRA_RDEPENDS = "kernel-image u-boot-extlinux"

INITRAMFS_IMAGE ?= "tegra-minimal-initramfs"
TEGRA_INITRAMFS_INITRD ?= "1"

TEGRA_AUDIO_DEVICE ?= "tegrahda"

SERIAL_CONSOLES ?= "115200;ttyS0"

KERNEL_MODULE_AUTOLOAD = "nvgpu"

KERNEL_ARGS ?= "sdhci_tegra.en_boot_part_access=1"

NVIDIA_CHIP = "0x21"

CUDA_NVCC_ARCH_FLAGS ?= "--gpu-architecture=compute_53 --gpu-code=sm_53"

require conf/machine/include/tegra-common.inc

L4T_BSP_NAME = "t210ref_release_aarch64"
L4T_SRCS_NAME = "Sources/T210"
L4T_BSP_PREFIX = "Tegra210"
L4T_DEB_SOCNAME = "t210"

NVPMODEL ?= "nvpmodel_t210"
TEGRA_BOARDID ?= "2180"
TEGRA_FAB ?= "100"
TEGRA_BOARDSKU ?= ""
TEGRA_BOARDREV ?= ""
TEGRA_CHIPREV ?= "0"
# Extracted from l4t_generate_soc_bup.sh for BOARDID=2180 and board=jetson-tx1
TEGRA_BUPGEN_SPECS ?= "fab=100 fab=410"
