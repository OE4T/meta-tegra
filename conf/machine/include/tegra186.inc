# Settings common to all Tegra X2 (tegra186) SoCs

SOC_FAMILY = "tegra186"

require conf/machine/include/soc-family.inc
require conf/machine/include/arm/arch-armv8a.inc

KERNEL_IMAGETYPE = "Image"

MACHINE_FEATURES = "alsa usbhost pci rtc cuda"
MACHINE_ESSENTIAL_EXTRA_RDEPENDS = "tegra-firmware ${@'' if d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('cboot') else 'kernel-devicetree kernel-image u-boot'}"
MACHINE_EXTRA_RDEPENDS = "tegra-nvpmodel tegra-nvphs tegra-nvs-service tegra-nvstartup tegra-configs-udev"
MACHINE_EXTRA_RRECOMMENDS = "kernel-module-nvgpu kernel-module-tegra-udrm"

INITRAMFS_IMAGE ?= "tegra-minimal-initramfs"
TEGRA_INITRAMFS_INITRD ?= "${@'' if d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('cboot') else '1'}"
INITRAMFS_IMAGE_BUNDLE ?= "${@'1' if d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('cboot') else ''}"
IMAGE_CLASSES_append = "${@' image_types_cboot' if d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('cboot') else ''}"
IMAGE_UBOOT ?= "${@'' if d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('cboot') else 'u-boot'}"

TEGRA_AUDIO_DEVICE ?= "tegrahda"

SERIAL_CONSOLE = "115200 ttyS0"

NVIDIA_CHIP = "0x18"

TEGRA_SIGNING_ENV ?= "BOARDID=${TEGRA_BOARDID} FAB=${TEGRA_FAB}"

CUDA_VERSION ?= "10.0"
CUDA_NVCC_ARCH_FLAGS ?= "--gpu-architecture=compute_62 --gpu-code=sm_62"

PREFERRED_PROVIDER_u-boot-fw-utils = "u-boot-fw-utils-tegra"
PREFERRED_RPROVIDER_u-boot-fw-utils = "u-boot-fw-utils-tegra"
PREFERRED_PROVIDER_tos-trusty ?= "tos-prebuilt"

require conf/machine/include/tegra-common.inc

L4T_BSP_NAME = "t186ref_release_aarch64"
L4T_SRCS_NAME = "Sources/T186"
L4T_BSP_PREFIX = "Tegra186"

NVPMODEL ?= "nvpmodel_t186"
