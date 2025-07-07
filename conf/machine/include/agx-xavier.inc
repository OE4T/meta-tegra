require conf/machine/include/tegra194.inc

KERNEL_ARGS ?= "mminit_loglevel=4 console=tty0 console=ttyTCU0,115200 fbcon=map:0 video=efifb:off"

EMMC_SIZE ?= "31276924928"
EMMC_DEVSECT_SIZE ?= "512"
BOOTPART_SIZE ?= "8388608"
BOOTPART_LIMIT ?= "10485760"
ROOTFSPART_SIZE_DEFAULT ?= "30064771072"
ODMDATA ?= "0x9190000"

EMMC_BCT_OVERRIDE ?= "tegra194-memcfg-sw-override.cfg"
NVIDIA_BOARD ?= "t186ref"

TEGRA_AUDIO_DEVICE ?= "tegra-hda-jetson-agx"
