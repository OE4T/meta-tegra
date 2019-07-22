# Settings common to all Tegra X1 (tegra210) SoCs

SOC_FAMILY = "tegra210"

require conf/machine/include/soc-family.inc
require conf/machine/include/arm/arch-armv8a.inc

KERNEL_IMAGETYPE = "Image"

MACHINE_FEATURES = "alsa usbhost pci rtc cuda"
MACHINE_ESSENTIAL_EXTRA_RDEPENDS = "tegra-firmware kernel-devicetree kernel-image u-boot"
MACHINE_EXTRA_RDEPENDS = "tegra-nvpmodel tegra-nvphs tegra-nvs-service tegra-configs-nvstartup tegra-configs-udev kernel-module-nvgpu kernel-module-nvs"

INITRAMFS_IMAGE ?= "tegra-minimal-initramfs"
TEGRA_INITRAMFS_INITRD ?= "1"

TEGRA_AUDIO_DEVICE ?= "tegrahda"

SERIAL_CONSOLE = "115200 ttyS0"

KERNEL_MODULE_AUTOLOAD = "nvgpu"

KERNEL_ARGS ?= "sdhci_tegra.en_boot_part_access=1"

NVIDIA_CHIP = "0x21"

DEB_PKG_EXTENSION = "arm64"

CUDA_VERSION ?= "10.0"
CUDA_NVCC_ARCH_FLAGS ?= "--gpu-architecture=compute_53 --gpu-code=sm_53"

PREFERRED_PROVIDER_libdrm = "libdrm-tegra-shim"
PREFERRED_PROVIDER_drm = "libdrm-tegra-shim"
PREFERRED_PROVIDER_u-boot-fw-utils = "u-boot-fw-utils-tegra"
PREFERRED_RPROVIDER_u-boot-fw-utils = "u-boot-fw-utils-tegra"

require conf/machine/include/tegra-common.inc

L4T_BSP_NAME = "jetson-nano"
L4T_BSP_BASEPFX = "Jetson-Nano"
L4T_BSP_PREFIX = "${L4T_BSP_BASEPFX}-Tegra210"
