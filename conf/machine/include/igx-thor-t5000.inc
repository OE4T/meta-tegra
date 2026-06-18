# Common settings for IGX Thor T5000 (p3834-0008) modules

TEGRA_BOARDID ?= "3834"
TEGRA_FAB ?= "400"
TEGRA_BOARDSKU ?= "0008"
TEGRA_BOARDREV ?= "G.5"
TEGRA_CHIPREV ?= "1"
NVPMODEL ?= "nvpmodel_p3834_0008"
NVFANCONTROL ?= "nvfancontrol_p3834_0008_p3740_0002"

TNSPEC_BOOTDEV ?= "nvme0n1p1"
PARTITION_LAYOUT_TEMPLATE_DEFAULT ?= "flash_l4t_t264_qspi.xml"
PARTITION_LAYOUT_TEMPLATE_DEFAULT_SUPPORTS_REDUNDANT ?= "1"
PARTITION_LAYOUT_EXTERNAL_DEFAULT ?= "flash_l4t_t264_nvme.xml"
TEGRAFLASH_NO_INTERNAL_STORAGE = "1"

TEGRA_BUPGEN_SPECS ?= " \
    fab=000;boardsku=0008;boardrev=;chiprev=;chipsku=00:00:00:A0;bup_type=bl \
    fab=401;boardsku=0008;boardrev=;chiprev=;chipsku=00:00:00:A0;bup_type=bl \
"

EMMC_SIZE ?= ""
BOOTPART_SIZE ?= ""

TEGRA_PLUGIN_MANAGER_OVERLAYS ?= ""
TEGRA_FLASHVAR_BPFDTB_FILE ?= "tegra264-bpmp-3834-0008-3740-xxxx.dtb"
TEGRA_FLASHVAR_BPF_FILE ?= "bpmp_t264-TA1090SA-A1_prod.bin"
TEGRA_FLASHVAR_CHIP_SKU ?= "00:00:00:A0"
TEGRA_FLASHVAR_DEVICEPROD_CONFIG ?= "tegra264-mb1-bct-cprod-p3834-xxxx-p3971-0000.dts"
TEGRA_FLASHVAR_DEVICE_CONFIG ?= "tegra264-mb1-bct-device-p3834-xxxx.dts"
TEGRA_FLASHVAR_DEV_PARAMS ?= "tegra264-br-bct-common-l4t.dts"
TEGRA_FLASHVAR_DEV_PARAMS_B ?= ""
TEGRA_FLASHVAR_EMC_FUSE_DEV_PARAMS ?= "tegra264-br-bct-diag-boot.dts"
TEGRA_FLASHVAR_FSIFW ?= "fsi-fw-t264.bin"
TEGRA_FLASHVAR_GPIOINT_CONFIG ?= "tegra264-mb1-bct-gpioint-p3834-xxxx-p3971-0000.dts"
TEGRA_FLASHVAR_MB2BCT_CFG ?= "tegra264-mb2-bct-misc-p3834-0008-p3740-0002.dts"
TEGRA_FLASHVAR_MINRATCHET_CONFIG ?= "tegra264-mb1-bct-ratchet-p3834-xxxx-p3971-0000.dts"
TEGRA_FLASHVAR_MISC_CONFIG ?= "tegra264-mb1-bct-misc-p3834-xxxx-p3971-0000.dts"
TEGRA_FLASHVAR_PINMUX_CONFIG ?= "tegra264-mb1-bct-pinmux-p3834-0008-p3740-0002.dts"
TEGRA_FLASHVAR_PMC_CONFIG ?= "tegra264-mb1-bct-padvoltage-p3834-0008-p3740-0002.dts"
TEGRA_FLASHVAR_PMIC_CONFIG ?= "tegra264-mb1-bct-pmic-p3834-0008-p3740-0002.dts"
TEGRA_FLASHVAR_PROD_CONFIG ?= "tegra264-mb1-bct-prod-p3834-xxxx-p3971-0000.dts"
TEGRA_FLASHVAR_RAMCODE ?= "12"
TEGRA_FLASHVAR_SCR_CONFIG ?= "tegra264-mb2-bct-firewall-p3834-xxxx-p3971-0000.dts"
TEGRA_FLASHVAR_UPHY_CONFIG ?= ""
TEGRA_FLASHVAR_WB0SDRAM_BCT ?= "tegra264-p3834-0008-sdram-bct-warmboot-l4t.dts"
TEGRA_FLASHVAR_BPMP_MEM_CONFIG ?= "tegra264-p3834-0008-sdram-dfs.dts"

TEGRA_EDK2_CONFIGURATION ?= "general_igx"

require conf/machine/include/tegra264.inc

MACHINE_EXTRA_RRECOMMENDS += "kernel-module-r8126 kernel-module-8168 kernel-module-8169 kernel-module-realtek kernel-module-ina238"
MACHINE_EXTRA_RRECOMMENDS += "kernel-module-ramoops kernel-module-tsecriscv"
MACHINE_EXTRA_RDEPENDS += "linux-firmware-rtl8168"

KERNEL_DEVICETREE ?= "tegra264-p3740-0002+p3834-0008-qspi-nvme.dtb"
# 50GiB default rootfs size
ROOTFSPART_SIZE_DEFAULT ?= "53687091200"
TEGRA_FLASHVAR_ODMDATA ?= "uphy0-config-6,pcie@3_status=okay"
EMC_BCT ?= "tegra264-p3834-0008-sdram-bct-l4t.dts"
TEGRA_AUDIO_DEVICE ?= "tegra-hda-p3767-p3509"

OTABOOTDEV ?= "/dev/mtdblock0"
OTAGPTDEV ?= "/dev/mtdblock0"
