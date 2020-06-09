# Settings common to all Tegra X2 (tegra186) SoCs

SOC_FAMILY = "tegra186"

require conf/machine/include/soc-family.inc
require conf/machine/include/arm/arch-armv8a.inc

KERNEL_IMAGETYPE = "Image"

MACHINE_FEATURES = "alsa usbhost pci rtc cuda"
MACHINE_ESSENTIAL_EXTRA_RDEPENDS = "tegra-firmware ${@'' if d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('cboot') else 'kernel-devicetree kernel-image u-boot'}"
MACHINE_EXTRA_RDEPENDS = "tegra-nvpmodel tegra-nvphs tegra-nvs-service tegra-nvstartup tegra-configs-udev"
MACHINE_EXTRA_RRECOMMENDS = "kernel-module-nvgpu kernel-module-tegra-udrm"

INITRAMFS_IMAGE ?= "${@'' if d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('cboot') else 'tegra-minimal-initramfs'}"
TEGRA_INITRAMFS_INITRD ?= "${@'' if d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('cboot') else '1'}"
INITRD_IMAGE ?= "${@'tegra-minimal-initramfs' if d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('cboot') else ''}"
INITRD_FSTYPES ?= "${@'cpio.gz.cboot cpio.gz.cboot.bup-payload' if d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('cboot') else '${INITRAMFS_FSTYPES}'}"
IMAGE_UBOOT ?= "${@'' if d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('cboot') else 'u-boot'}"

TEGRA_AUDIO_DEVICE ?= "tegrahda"

SERIAL_CONSOLE = "115200 ttyS0"

KERNEL_MODULE_AUTOLOAD = "nvgpu tegra-udrm"
KERNEL_MODULE_PROBECONF = "tegra-udrm"
module_conf_tegra-udrm = "options tegra-udrm modeset=1"


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
