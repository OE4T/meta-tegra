# Settings common to all Orin (tegra234) SoCs

SOC_FAMILY = "tegra234"

require conf/machine/include/soc-family.inc
DEFAULTTUNE ?= "armv8a-crc"
require conf/machine/include/arm/arch-armv8-2a.inc

INITRAMFS_IMAGE ?= "tegra-minimal-initramfs"
INITRAMFS_IMAGE_BUNDLE ?= "0"
INITRAMFS_FSTYPES:append = "${@'' if d.getVar('INITRAMFS_IMAGE_BUNDLE') == '1' else ' cpio.gz.cboot cpio.gz.cboot.bup-payload'}"

SERIAL_CONSOLES ?= "115200;ttyTCU0"

KERNEL_MODULE_AUTOLOAD ?= "nvgpu nvidia nvidia-drm"

NVIDIA_CHIP = "0x23"

TEGRA_BOARDID ?= "3701"
TEGRA_FAB ?= "TS4"
TEGRA_BOARDSKU ?= "0000"
TEGRA_BOARDREV ?= "C.0"
TEGRA_CHIPREV ?= "0"
TEGRA_SIGNING_ENV = "CHIPREV=${TEGRA_CHIPREV} BOARDID=${TEGRA_BOARDID} FAB=${TEGRA_FAB} BOARDSKU=${TEGRA_BOARDSKU} BOARDREV=${TEGRA_BOARDREV}"
TEGRA_CUDA_ARCHITECTURE = "72"

require conf/machine/include/tegra-common.inc

EFI_PROVIDER ?= "l4t-launcher"

L4T_BSP_PREFIX = "Jetson"
L4T_DEB_SOCNAME = "t234"

NVPMODEL ?= "nvpmodel_p3701_0000"
NVPOWER ?= "jetsonpower_t234"
NVFANCONTROL ?= "nvfancontrol_p3701_0000"

MACHINE_EXTRA_RRECOMMENDS += "kernel-module-nvidia kernel-module-nvidia-drm kernel-module-nvidia-modeset"
