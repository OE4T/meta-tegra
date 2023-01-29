# Settings common to all Orin (tegra234) SoCs

SOC_FAMILY = "tegra234"

require conf/machine/include/soc-family.inc
DEFAULTTUNE ?= "armv8a-crc"
require conf/machine/include/arm/arch-armv8-2a.inc

SERIAL_CONSOLES ?= "115200;ttyTCU0"

KERNEL_MODULE_AUTOLOAD ?= "nvmap nvgpu pwm-fan ina3221 nvidia-drm"

NVIDIA_CHIP = "0x23"

TEGRA_BOARDID ?= "3701"
TEGRA_FAB ?= "TS4"
TEGRA_BOARDSKU ?= "0000"
TEGRA_BOARDREV ?= "C.0"
TEGRA_CHIPREV ?= "0"
TEGRA_CUDA_ARCHITECTURE ?= "87"

require conf/machine/include/tegra-common.inc

L4T_BSP_PREFIX = "Jetson"
L4T_DEB_SOCNAME = "t234"

NVPMODEL ?= "nvpmodel_p3701_0000"
NVPOWER ?= "jetsonpower_t234"
NVFANCONTROL ?= "nvfancontrol_p3701_0000"

MACHINE_EXTRA_RRECOMMENDS += "kernel-module-nvidia kernel-module-nvidia-drm kernel-module-nvidia-modeset kernel-module-userspace-alert"

PARTITION_LAYOUT_EXTERNAL ?= "flash_l4t_t234_nvme.xml"
