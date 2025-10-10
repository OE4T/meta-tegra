# Settings common to all Thor (tegra264) SoCs

SOC_FAMILY = "tegra264"

require conf/machine/include/soc-family.inc

# t264 uses Neoverse V3AE cores, but default to
# generic aarch64 to match how Jetson Linux/Ubuntu
# libraries are built
DEFAULTTUNE ?= "aarch64"
require conf/machine/include/arm/arch-armv9a.inc

SERIAL_CONSOLES ?= "115200;ttyUTC0"

KERNEL_MODULE_AUTOLOAD ?= "nvmap pwm-fan ina3221"
KERNEL_ARGS ?= "mminit_loglevel=4 earlycon=tegra_utc,mmio32,0xc5a0000 console=ttyUTC0,115200 clk_ignore_unused firmware_class.path=/etc/firmware fbcon=map:0 efi=runtime"

NVIDIA_CHIP = "0x26"

TEGRA_BOARDID ?= "3834"
TEGRA_FAB ?= "400"
TEGRA_BOARDSKU ?= "0008"
TEGRA_BOARDREV ?= "G.5"
TEGRA_CHIPREV ?= "1"
TEGRA_CUDA_ARCHITECTURE ?= "110"

TEGRA_FLASHVAR_BR_CMD_CONFIG ?= ""

require conf/machine/include/tegra-common.inc

L4T_BSP_PREFIX = "Jetson"
L4T_DEB_SOCNAME = "som"

NVPMODEL ?= "nvpmodel_p3834_0008"
NVPOWER ?= "jetsonpower_t264"
NVFANCONTROL ?= "nvfancontrol_p3834_0008_p4071_0000"

PARTITION_LAYOUT_EXTERNAL_DEFAULT ?= "flash_l4t_t264_nvme.xml"
PARTITION_LAYOUT_RCMBOOT_DEFAULT ?= "flash_l4t_t264_rcmboot.xml"

TEGRA_BOOT_FIRMWARE_FILES = "\
    adsp-fw.bin \
    applet_t264.bin \
    camera-rtcpu-t264-rce.img \
    mb1_t264_prod.bin \
    mb2_t264.bin \
    mb2rf_t264.bin \
    bl31_t264.fip \
    psc_bl1_t264_prod.bin \
    hpse_bl1_t264_prod.bin \
    sb_bl1_t264_prod.bin \
    hpsefw_t264_prod.bin \
    sbfw_t264_prod.bin \
    pscfw_t264_prod.bin \
    hpseraw_t264_prod.bin \
    sbraw_t264_prod.bin \
    tsec_t264_prod.bin \
    sc7_t264_prod.bin \
    display-t264-dce.bin \
    nv-rce1-t264.bin \
    aon-fw_t264.bin \
    adsp0-fw_t264.bin \
    adsp1-fw_t264.bin \
    xusb_t264_prod.bin \
    igbfw_gb10b_gsc_package_prod.bin \
    nvpva_030.fw \
    spvault-hpse-oem-conf.bin \
    spvault-sb-oem-conf.bin \
    readinfo_t264_min_prod.xml \
    flash_l4t_t264_bct_cfg.xml \
"

PREFERRED_PROVIDER_hafnium ?= "hafnium"
PREFERRED_VERSION_hafnium ?= "2.9-l4t%"
