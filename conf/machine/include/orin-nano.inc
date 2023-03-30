TEGRA_BOARDID ?= "3767"
TEGRA_FAB ?= "RC1"
TEGRA_BOARDSKU ?= "0005"
TEGRA_BOARDREV ?= "B.4"
TEGRA_CHIPREV ?= "1"
NVPMODEL ?= "nvpmodel_p3767_0003"
NVFANCONTROL ?= "nvfancontrol_p3767_0000"

MACHINEOVERRIDES =. "orin-nano:"
require conf/machine/include/tegra234.inc

TEGRA_BUPGEN_SPECS ?= "fab=000;boardsku=0000;boardrev=;bup_type=bl \
                       fab=000;boardsku=0000;boardrev=;bup_type=kernel \
                       fab=000;boardsku=0001;boardrev=;bup_type=bl \
                       fab=000;boardsku=0001;boardrev=;bup_type=kernel \
                       fab=000;boardsku=0003;boardrev=;bup_type=bl \
                       fab=000;boardsku=0003;boardrev=;bup_type=kernel \
                       fab=000;boardsku=0004;boardrev=;bup_type=bl \
                       fab=000;boardsku=0004;boardrev=;bup_type=kernel \
                       fab=000;boardsku=0005;boardrev=;bup_type=bl \
                       fab=000;boardsku=0005;boardrev=;bup_type=kernel"

KERNEL_DEVICETREE ?= "tegra234-p3767-0003-p3768-0000-a0.dtb"
KERNEL_ARGS ?= "mminit_loglevel=4 console=ttyTCU0,115200 firmware_class.path=/etc/firmware fbcon=map:0 net.ifnames=0"
MACHINE_EXTRA_RRECOMMENDS += "kernel-module-r8168 kernel-module-r8169 kernel-module-realtek"
MACHINE_EXTRA_RDEPENDS += "linux-firmware-rtl8168"

EMMC_SIZE ?= ""
BOOTPART_SIZE ?= "8388608"
ROOTFSPART_SIZE ?= "30064771072"
ODMDATA ?= "gbe-uphy-config-8,hsstp-lane-map-3,hsio-uphy-config-0"
EMMC_BCT ?= "tegra234-p3767-0000-sdram-l4t.dts"
NVIDIA_BOARD ?= "t186ref"
TEGRA_AUDIO_DEVICE ?= "tegra-hda-p3767-p3509"

OTABOOTDEV ?= "/dev/mtdblock0"
OTAGPTDEV ?= "/dev/mtdblock0"

TEGRA_PLUGIN_MANAGER_OVERLAYS ?= "tegra234-p3767-overlay.dtbo"
