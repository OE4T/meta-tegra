# Settings common to all Tegra X2 (tegra186) SoCs

SOC_FAMILY = "tegra186"

require conf/machine/include/soc-family.inc
require conf/machine/include/arm/arch-armv8.inc

KERNEL_IMAGETYPE = "Image"

MACHINE_FEATURES = "alsa bluetooth usbhost pci wifi rtc cuda"
MACHINE_ESSENTIAL_EXTRA_RDEPENDS = "tegra-firmware ${@'' if d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('cboot') else 'kernel-devicetree kernel-image u-boot'}"
MACHINE_EXTRA_RDEPENDS = "tegra-nvpmodel tegra-nvphs tegra-nvs-service tegra-configs-nvstartup tegra-configs-udev kernel-module-nvgpu kernel-module-nvs"
MACHINE_EXTRA_RRECOMMENDS += "tegra-firmware-brcm kernel-module-bcmdhd"

INITRAMFS_IMAGE ?= "${@'' if d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('cboot') else 'tegra-minimal-initramfs'}"
TEGRA_INITRAMFS_INITRD ?= "${@'' if d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('cboot') else '1'}"
INITRD_IMAGE ?= "${@'tegra-minimal-initramfs' if d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('cboot') else ''}"
INITRD_FSTYPES ?= "${@'cpio.gz.cboot cpio.gz.cboot.bup-payload' if d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('cboot') else '${INITRAMFS_FSTYPES}'}"
IMAGE_CLASSES_append = "${@' image_types_cboot' if d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('cboot') else ''}"
IMAGE_UBOOT ?= "${@'' if d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('cboot') else 'u-boot'}"

TEGRA_AUDIO_DEVICE ?= "tegrahda"

SERIAL_CONSOLE = "115200 ttyS0"

KERNEL_MODULE_AUTOLOAD = "nvgpu"

NVIDIA_CHIP = "0x18"

TEGRA_FAB ?= "B02"
TEGRA_BOARDID ?= "3310"

DEB_PKG_EXTENSION = "arm64"

CUDA_VERSION ?= "10.0"
CUDA_NVCC_ARCH_FLAGS ?= "--gpu-architecture=compute_62 --gpu-code=sm_62"

PREFERRED_PROVIDER_libdrm = "libdrm-tegra-shim"
PREFERRED_PROVIDER_drm = "libdrm-tegra-shim"
PREFERRED_PROVIDER_u-boot-fw-utils = "u-boot-fw-utils-tegra"
PREFERRED_RPROVIDER_u-boot-fw-utils = "u-boot-fw-utils-tegra"
PREFERRED_PROVIDER_cboot ?= "cboot-prebuilt"

PREFERRED_VERSION_xserver-xorg = "1.19%"

require conf/machine/include/tegra-common.inc

L4T_BSP_NAME = "jax-tx2"
L4T_BSP_BASEPFX = "JAX-TX2"
L4T_BSP_PREFIX = "${L4T_BSP_BASEPFX}-Jetson"
