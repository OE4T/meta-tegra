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
KERNEL_ARGS ?= "console=ttyTCU0,115200 console=tty0 fbcon=map:0"

EMMC_DEVSECT_SIZE ?= "512"
BOOTPART_SIZE ?= ""
BOOTPART_LIMIT ?= "10485760"
ROOTFSPART_SIZE ?= "15032385536"
RECROOTFSSIZE ?= "104857600"
ODMDATA ?= "0xB8190000"
EMMC_BCT ?= "tegra194-mb1-bct-memcfg-p3668-0001-a00.cfg"
EMMC_BCT_OVERRIDE ?= "tegra194-memcfg-sw-override.cfg"
NVIDIA_BOARD ?= "t186ref"
TEGRA_PLUGIN_MANAGER_OVERLAYS ?= "tegra194-p3668-p3509-overlay.dtbo"

OTABOOTDEV ?= "/dev/mtdblock0"
OTAGPTDEV ?= "/dev/mtdblock0"

TEGRA_AUDIO_DEVICE ?= "tegra-hda-jetson-xnx"
