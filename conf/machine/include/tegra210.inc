# Settings common to all Tegra X1 (tegra210) SoCs

SOC_FAMILY = "tegra210"

require conf/machine/include/soc-family.inc
require conf/machine/include/arm/arch-armv8.inc

KERNEL_IMAGETYPE = "Image"

MACHINE_FEATURES = "alsa bluetooth usbhost pci wifi rtc cuda"
MACHINE_ESSENTIAL_EXTRA_RDEPENDS = "tegra-firmware kernel-devicetree kernel-image"
MACHINE_EXTRA_RDEPENDS = "tegra-configs-nvstartup tegra-configs-udev"
MACHINE_EXTRA_RRECOMMENDS = "tegra-firmware-brcm kernel-module-bcmdhd"

INITRAMFS_IMAGE ?= "tegra210-minimal-initramfs"
TEGRA_INITRAMFS_INITRD ?= "1"

TEGRA_AUDIO_DEVICE ?= "tegrahda"

SERIAL_CONSOLE = "115200 ttyS0"

KERNEL_MODULE_AUTOLOAD = ""

NVIDIA_CHIP = "0x21"

DEB_PKG_EXTENSION = "arm64"

CUDA_VERSION ?= "9.0"
CUDA_NVCC_ARCH_FLAGS ?= "--gpu-architecture=compute_53 --gpu-code=sm_53"

require conf/machine/include/tegra-common.inc
