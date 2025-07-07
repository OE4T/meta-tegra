# Common settings for Xavier NX modules and P3509 carriers
# (with PCI WiFi/USB Bluetooth). Should also be compatible
# with P3489 carriers.

TEGRA_BOARDID ?= "3668"
TEGRA_FAB ?= "200"
TEGRA_BOARDREV ?= "G.0"
TEGRA_CHIPREV ?= "2"
NVPMODEL ?= "nvpmodel_t194_p3668"
NVFANCONTROL ?= "nvfancontrol_p3668"
TEGRA_AUDIO_DEVICE ?= "tegra-hda-jetson-xnx"

MACHINEOVERRIDES =. "xavier-nx:"
require conf/machine/include/tegra194.inc

KERNEL_DEVICETREE ?= "tegra194-p3668-all-p3509-0000.dtb"
KERNEL_ARGS ?= "console=ttyTCU0,115200 console=tty0 fbcon=map:0 video=efifb:off"

EMMC_DEVSECT_SIZE ?= "512"
BOOTPART_SIZE ?= ""
BOOTPART_LIMIT ?= "10485760"
ROOTFSPART_SIZE_DEFAULT ?= "15032385536"
RECROOTFSSIZE ?= "104857600"
ODMDATA ?= "0xB8190000"
EMMC_BCT ?= "tegra194-mb1-bct-memcfg-p3668-0001-a00.cfg"
EMMC_BCT_OVERRIDE ?= "tegra194-memcfg-sw-override.cfg"
NVIDIA_BOARD ?= "t186ref"
TEGRA_PLUGIN_MANAGER_OVERLAYS ?= "tegra194-p3668-p3509-overlay.dtbo"

OTABOOTDEV ?= "/dev/mtdblock0"
OTAGPTDEV ?= "/dev/mtdblock0"

TEGRA_AUDIO_DEVICE ?= "tegra-hda-jetson-xnx"

TEGRA_FLASHVAR_BPFDTB_FILE ?= "tegra194-a02-bpmp-p3668-a00_lz4.dtb"
TEGRA_FLASHVAR_BR_CMD_CONFIG ?= "tegra194-mb1-bct-reset-p3668-0001-a00.cfg"
TEGRA_FLASHVAR_DEVICE_CONFIG ?= "tegra19x-mb1-bct-device-qspi-p3668.cfg"
TEGRA_FLASHVAR_DEV_PARAMS ?= "tegra194-br-bct-qspi-l4t.cfg"
TEGRA_FLASHVAR_DEV_PARAMS_B ?= "tegra194-br-bct_b-qspi-l4t.cfg"
TEGRA_FLASHVAR_GPIOINT_CONFIG ?= "tegra194-mb1-bct-gpioint-p3668-0001-a00.cfg"
TEGRA_FLASHVAR_MINRATCHET_CONFIG ?= "--minratchet_config tegra194-mb1-bct-ratchet-p3668.cfg"
TEGRA_FLASHVAR_MISC_COLD_BOOT_CONFIG ?= "tegra194-mb1-bct-misc-l4t.cfg"
TEGRA_FLASHVAR_MISC_CONFIG ?= "tegra194-mb1-bct-misc-flash.cfg"
TEGRA_FLASHVAR_PINMUX_CONFIG ?= "tegra19x-mb1-pinmux-p3668-a01.cfg"
TEGRA_FLASHVAR_PMC_CONFIG ?= "tegra19x-mb1-padvoltage-p3668-a01.cfg"
TEGRA_FLASHVAR_PMIC_CONFIG ?= "tegra194-mb1-bct-pmic-p3668-0001-a00.cfg"
TEGRA_FLASHVAR_PROD_CONFIG ?= "tegra19x-mb1-prod-p3668-0001-a00.cfg"
TEGRA_FLASHVAR_SCR_COLD_BOOT_CONFIG ?= "tegra194-mb1-bct-scr-cbb-mini-p3668.cfg"
TEGRA_FLASHVAR_SCR_CONFIG ?= "tegra194-mb1-bct-scr-cbb-mini-p3668.cfg"
