# Settings common to all Xavier (tegra194) SoCs

SOC_FAMILY = "tegra194"

require conf/machine/include/soc-family.inc
DEFAULTTUNE ?= "armv8a-crc"
require conf/machine/include/arm/arch-armv8-2a.inc

SERIAL_CONSOLES ?= "115200;ttyTCU0"

KERNEL_MODULE_AUTOLOAD ?= "nvmap nvgpu pwm-fan ina3221 tegra-udrm"

NVIDIA_CHIP = "0x19"

TEGRA_BOARDID ?= "2888"
TEGRA_FAB ?= "400"
TEGRA_BOARDSKU ?= "0001"
TEGRA_BOARDREV ?= "H.0"
TEGRA_CHIPREV ?= "2"

TEGRA_CUDA_ARCHITECTURE ?= "72"

require conf/machine/include/tegra-common.inc

L4T_BSP_PREFIX = "Jetson"
L4T_DEB_SOCNAME = "t194"

NVPMODEL ?= "nvpmodel_t194"
NVPOWER ?= "jetsonpower_t194"
NVFANCONTROL ?= "nvfancontrol_p2888"

MACHINE_EXTRA_RRECOMMENDS += "kernel-module-tegra-udrm"

KERNEL_MODULE_PROBECONF += "tegra-udrm"
module_conf_tegra-udrm = "options tegra-udrm modeset=1"

PARTITION_LAYOUT_EXTERNAL ?= "flash_l4t_nvme.xml"
