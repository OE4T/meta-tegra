# Common settings for AGX Orin modules

require conf/machine/include/tegra234.inc

KERNEL_ARGS ?= "mminit_loglevel=4 console=tty0 console=ttyTCU0,115200 firmware_class.path=/etc/firmware fbcon=map:0 video=efifb:off console=tty0 efi=runtime nvme.use_threaded_interrupts=1 pci=pcie_bus_perf"

EMMC_DEVSECT_SIZE ?= "512"
BOOTPART_SIZE ?= "8388608"
BOOTPART_LIMIT ?= "10485760"
ROOTFSPART_SIZE_DEFAULT ?= "30064771072"
ODMDATA ?= "gbe-uphy-config-22,hsstp-lane-map-3,nvhs-uphy-config-0,hsio-uphy-config-0,gbe0-enable-10g"
EMMC_BCT ?= "tegra234-p3701-0000-sdram-l4t.dts"
PARTITION_LAYOUT_TEMPLATE_DEFAULT ?= "flash_t234_qspi_sdmmc.xml"
TEGRA_AUDIO_DEVICE ?= "tegra-hda-jetson-agx"

OTABOOTDEV ?= "/dev/mtdblock0"
OTAGPTDEV ?= "/dev/mtdblock0"

TEGRA_PLUGIN_MANAGER_OVERLAYS ?= "tegra234-p3737-0000+p3701-0000-dynamic.dtbo tegra234-carveouts.dtbo tegra-optee.dtbo tegra234-p3737-camera-dual-imx274-overlay.dtbo tegra234-p3737-camera-e3331-overlay.dtbo tegra234-p3737-camera-e3333-overlay.dtbo tegra234-p3737-camera-imx185-overlay.dtbo"

TEGRA_FLASHVAR_BPFDTB_FILE ?= "tegra234-bpmp-3701-0000-3737-0000.dtb"
TEGRA_FLASHVAR_BPF_FILE ?= "bpmp_t234-TE990M-A1_prod.bin"
TEGRA_FLASHVAR_BR_CMD_CONFIG ?= "tegra234-mb1-bct-reset-p3701-0000.dts"
TEGRA_FLASHVAR_CHIP_SKU ?= "00:00:00:D0"
TEGRA_FLASHVAR_DEVICEPROD_CONFIG ?= "tegra234-mb1-bct-cprod-p3701-0000.dts"
TEGRA_FLASHVAR_DEVICE_CONFIG ?= "tegra234-mb1-bct-device-p3701-0000.dts"
TEGRA_FLASHVAR_DEV_PARAMS ?= "tegra234-br-bct-p3701-0000.dts"
TEGRA_FLASHVAR_DEV_PARAMS_B ?= "tegra234-br-bct_b-p3701-0000.dts"
TEGRA_FLASHVAR_EMC_FUSE_DEV_PARAMS ?= "tegra234-br-bct-diag-boot.dts"
TEGRA_FLASHVAR_GPIOINT_CONFIG ?= "tegra234-mb1-bct-gpioint-p3701-0000.dts"
TEGRA_FLASHVAR_MB2BCT_CFG ?= "tegra234-mb2-bct-misc-p3701-0000.dts"
TEGRA_FLASHVAR_MINRATCHET_CONFIG ?= "--minratchet_config tegra234-mb1-bct-ratchet-p3701-0000.dts"
TEGRA_FLASHVAR_MISC_CONFIG ?= "tegra234-mb1-bct-misc-p3701-0000.dts"
TEGRA_FLASHVAR_PINMUX_CONFIG ?= "tegra234-mb1-bct-pinmux-p3701-0000-a04.dtsi"
TEGRA_FLASHVAR_PMC_CONFIG ?= "tegra234-mb1-bct-padvoltage-p3701-0000-a04.dtsi"
TEGRA_FLASHVAR_PMIC_CONFIG ?= "tegra234-mb1-bct-pmic-p3701-@PMICBOARDSKU@.dts"
TEGRA_FLASHVAR_PROD_CONFIG ?= "tegra234-mb1-bct-prod-p3701-0000.dts"
TEGRA_FLASHVAR_SCR_CONFIG ?= "tegra234-mb2-bct-scr-p3701-0000.dts"
TEGRA_FLASHVAR_UPHY_CONFIG ?= "--uphy tegra234-mb1-bct-uphylane-si.dtsi"
TEGRA_FLASHVAR_WB0SDRAM_BCT ?= "tegra234-p3701-0000-wb0sdram-l4t.dts"
