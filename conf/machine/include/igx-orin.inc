# Common settings for IGX Orin modules

NVPMODEL ?= "nvpmodel_p3701_0008"
NVFANCONTROL ?= "nvfancontrol_p3701_0008"
TEGRA_EDK2_CONFIGURATION ?= "general_igx"

require conf/machine/include/tegra234.inc

KERNEL_ARGS ?= "mminit_loglevel=4 console=tty0 console=ttyTCU0,115200 firmware_class.path=/etc/firmware fbcon=map:0 video=efifb:off console=tty0 efi=runtime nvme.use_threaded_interrupts=1 pci=pcie_bus_safe"

EMMC_DEVSECT_SIZE ?= "512"
BOOTPART_SIZE ?= "8388608"
BOOTPART_LIMIT ?= "10485760"
ROOTFSPART_SIZE_DEFAULT ?= "55834574848"
TEGRA_FLASHVAR_ODMDATA ?= "gbe-uphy-config-8,hsio-uphy-config-0,nvhs-uphy-config-0"
EMC_BCT ?= "tegra234-p3701-0008-sdram-l4t.dts"
PARTITION_LAYOUT_TEMPLATE_DEFAULT ?= "flash_t234_qspi_sdmmc_safety.xml"
TEGRA_AUDIO_DEVICE ?= "tegra-hda-jetson-agx"

OTABOOTDEV ?= "/dev/mtdblock0"
OTAGPTDEV ?= "/dev/mtdblock0"

TEGRA_PLUGIN_MANAGER_OVERLAYS ?= "tegra234-carveouts.dtbo tegra-optee.dtbo"

TEGRA_FLASHVAR_BPFDTB_FILE ?= "tegra234-bpmp-3701-0008-3740-0002-c00.dtb"
TEGRA_FLASHVAR_BPF_FILE ?= "bpmp_t234-TA990SA-A1_prod.bin"
TEGRA_FLASHVAR_BR_CMD_CONFIG ?= "tegra234-mb1-bct-reset-p3701-0002-p3740-0002.dts"
TEGRA_FLASHVAR_CHIP_SKU ?= "00:00:00:D0"
TEGRA_FLASHVAR_DEVICEPROD_CONFIG ?= "tegra234-mb1-bct-cprod-p3701-0002-p3740-0002.dts"
TEGRA_FLASHVAR_DEVICE_CONFIG ?= "tegra234-mb1-bct-device-p3701-0002-p3740-0002.dts"
TEGRA_FLASHVAR_DEV_PARAMS ?= "tegra234-br-bct-p3701-0002-p3740-0002.dts"
TEGRA_FLASHVAR_DEV_PARAMS_B ?= "tegra234-br-bct_b-p3701-0002-p3740-0002.dts"
TEGRA_FLASHVAR_EMC_FUSE_DEV_PARAMS ?= "tegra234-br-bct-diag-boot.dts"
TEGRA_FLASHVAR_FSIFW ?= "fsi-lk.bin"
TEGRA_FLASHVAR_GPIOINT_CONFIG ?= "tegra234-mb1-bct-gpioint-p3701-0002-p3740-0002.dts"
TEGRA_FLASHVAR_MB2BCT_CFG ?= "tegra234-mb2-bct-misc-p3701-0002-p3740-0002.dts"
TEGRA_FLASHVAR_MINRATCHET_CONFIG ?= "--minratchet_config tegra234-mb1-bct-ratchet-p3701-0000.dts"
TEGRA_FLASHVAR_MISC_CONFIG ?= "tegra234-mb1-bct-misc-p3701-0008-flash.dts"
TEGRA_FLASHVAR_PINMUX_CONFIG ?= "tegra234-mb1-bct-pinmux-p3701-0008-p3740-0002-c01.dtsi"
TEGRA_FLASHVAR_PMC_CONFIG ?= "tegra234-mb1-bct-padvoltage-p3701-0002-p3740-0002.dtsi"
TEGRA_FLASHVAR_PMIC_CONFIG ?= "tegra234-mb1-bct-pmic-p3701-0008-p3740-0002-c01.dts"
TEGRA_FLASHVAR_PROD_CONFIG ?= "tegra234-mb1-bct-prod-p3701-0002-p3740-0002.dts"
TEGRA_FLASHVAR_SCR_CONFIG ?= "tegra234-mb2-bct-firewall-si-p3701-0002-p3740-0002.dts"
TEGRA_FLASHVAR_UPHY_CONFIG ?= ""
TEGRA_FLASHVAR_WB0SDRAM_BCT ?= "tegra234-p3701-0008-wb0sdram-l4t.dts"
