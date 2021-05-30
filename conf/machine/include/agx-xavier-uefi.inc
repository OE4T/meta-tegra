SERIAL_CONSOLES ?= "115200;ttyS0"

PREFERRED_PROVIDER_virtual/bootloader ?= "jetson-uefi-prebuilt"
PREFERRED_PROVIDER_virtual/kernel ?= "linux-tegra-upstream"

require conf/machine/include/tegra194.inc

MACHINE_ESSENTIAL_EXTRA_RDEPENDS = "linux-firmware-nvidia-tegra"
KERNEL_MODULE_AUTOLOAD = ""

KERNEL_ROOTSPEC ?= ""
KERNEL_DEVICETREE ?= "nvidia/tegra194-p2972-0000.dtb"
KERNEL_ARGS ?= "earlycon=uart8250,mmio32,0x3110000 console=ttyS0,115200n8 fbcon=map:0 ${KERNEL_ROOTSPEC}"

EMMC_SIZE ?= "31276924928"
EMMC_DEVSECT_SIZE ?= "512"
BOOTPART_SIZE ?= "8388608"
BOOTPART_LIMIT ?= "10485760"
ROOTFSPART_SIZE ?= "30064771072"
ODMDATA ?= "0x9190000"
EMMC_BCT ?= "tegra194-mb1-bct-memcfg-p2888.cfg"
EMMC_BCT_OVERRIDE ?= "tegra194-memcfg-sw-override.cfg"
NVIDIA_BOARD ?= "t186ref"
PARTITION_LAYOUT_TEMPLATE ?= "flash_t194_uefi_sdmmc.xml"

TEGRA_INITRD_FIRMWARE ?= "linux-firmware-nvidia-tegra"
