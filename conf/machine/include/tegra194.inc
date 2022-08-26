# Settings common to all Xavier (tegra194) SoCs

SOC_FAMILY = "tegra194"

require conf/machine/include/soc-family.inc
DEFAULTTUNE ?= "armv8a-crc"
require conf/machine/include/arm/arch-armv8-2a.inc

INITRAMFS_IMAGE ?= "tegra-minimal-initramfs"
INITRAMFS_IMAGE_BUNDLE ?= "0"
INITRAMFS_FSTYPES:append = "${@'' if d.getVar('INITRAMFS_IMAGE_BUNDLE') == '1' else ' cpio.gz.cboot cpio.gz.cboot.bup-payload'}"

SERIAL_CONSOLES ?= "115200;ttyTCU0"

KERNEL_MODULE_AUTOLOAD ?= "nvgpu tegra-udrm"

NVIDIA_CHIP = "0x19"

TEGRA_BOARDID ?= "2888"
TEGRA_FAB ?= "400"
TEGRA_BOARDSKU ?= "0001"
TEGRA_BOARDREV ?= "H.0"
TEGRA_CHIPREV ?= "2"
TEGRA_SIGNING_ENV = "CHIPREV=${TEGRA_CHIPREV} BOARDID=${TEGRA_BOARDID} FAB=${TEGRA_FAB} BOARDSKU=${TEGRA_BOARDSKU} BOARDREV=${TEGRA_BOARDREV}"

TEGRA_CUDA_ARCHITECTURE = "72"

require conf/machine/include/tegra-common.inc

EFI_PROVIDER ?= "l4t-launcher"

L4T_BSP_PREFIX = "Jetson"
L4T_DEB_SOCNAME = "t194"

NVPMODEL ?= "nvpmodel_t194"
NVPOWER ?= "jetsonpower_t194"
NVFANCONTROL ?= "nvfancontrol_p2888"

MACHINE_EXTRA_RRECOMMENDS += "kernel-module-tegra-udrm"

KERNEL_MODULE_PROBECONF += "tegra-udrm"
module_conf_tegra-udrm = "options tegra-udrm modeset=1"
