# Common settings for AGX Orin modules

require conf/machine/include/tegra234.inc

KERNEL_ARGS ?= "mminit_loglevel=4 console=tty0 console=ttyTCU0,115200 firmware_class.path=/etc/firmware fbcon=map:0 net.ifnames=0"

EMMC_SIZE ?= "31276924928"
EMMC_DEVSECT_SIZE ?= "512"
BOOTPART_SIZE ?= "8388608"
BOOTPART_LIMIT ?= "10485760"
ROOTFSPART_SIZE_DEFAULT ?= "30064771072"
ODMDATA ?= "gbe-uphy-config-22,hsstp-lane-map-3,nvhs-uphy-config-0,hsio-uphy-config-0,gbe0-enable-10g"
EMMC_BCT ?= "tegra234-p3701-0000-sdram-l4t.dts"
NVIDIA_BOARD ?= "t186ref"
PARTITION_LAYOUT_TEMPLATE_DEFAULT ?= "flash_t234_qspi_sdmmc.xml"
TEGRA_AUDIO_DEVICE ?= "tegra-hda-jetson-agx"

OTABOOTDEV ?= "/dev/mtdblock0"
OTAGPTDEV ?= "/dev/mtdblock0"

TEGRA_PLUGIN_MANAGER_OVERLAYS ?= "tegra234-p3737-audio-codec-rt5658-40pin.dtbo tegra234-p3737-overlay.dtbo tegra234-p3701-overlay.dtbo tegra234-p3737-camera-dual-imx274-overlay.dtbo tegra234-p3737-camera-e3331-overlay.dtbo tegra234-p3737-camera-e3333-overlay.dtbo tegra234-p3737-camera-imx185-overlay.dtbo tegra234-p3737-camera-imx390-overlay.dtbo"
