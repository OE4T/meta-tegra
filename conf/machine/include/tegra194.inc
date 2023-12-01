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

PARTITION_LAYOUT_EXTERNAL_DEFAULT ?= "flash_l4t_nvme.xml"

TEGRA_BOOT_FIRMWARE_FILES = "\
    adsp-fw.bin \
    bpmp-2_t194.bin \
    camera-rtcpu-t194-rce.img \
    dram-ecc-t194.bin \
    mb1_t194_prod.bin \
    nvdisp-init.bin \
    nvtboot_applet_t194.bin \
    nvtboot_t194.bin \
    preboot_c10_prod_cr.bin \
    mce_c10_prod_cr.bin \
    mts_c10_prod_cr.bin \
    nvtboot_cpu_t194.bin \
    nvtboot_recovery_t194.bin \
    nvtboot_recovery_cpu_t194.bin \
    spe_t194.bin \
    xusb_sil_rel_fw \
    warmboot_t194_prod.bin \
    sce_t194.bin \
    dram-ecc-t194.bin \
"
