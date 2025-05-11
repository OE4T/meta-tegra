TEGRA_BOARDID ?= "3767"
TEGRA_FAB ?= "RC1"
TEGRA_BOARDSKU ?= "0005"
TEGRA_BOARDREV ?= "B.4"
NVPMODEL ?= "nvpmodel_p3767_0003_super"
NVFANCONTROL ?= "nvfancontrol_p3767_0000"

MACHINEOVERRIDES =. "orin-nano:"
require conf/machine/include/tegra234.inc

TEGRA_BUPGEN_SPECS ?= "fab=000;boardsku=0003;boardrev=;chipsku=00:00:00:D5;bup_type=bl \
                       fab=000;boardsku=0003;boardrev=;bup_type=kernel \
                       fab=000;boardsku=0005;boardrev=;chipsku=00:00:00:D5;bup_type=bl \
                       fab=000;boardsku=0005;boardrev=;bup_type=kernel"

KERNEL_DEVICETREE ?= "tegra234-p3767-0003-super-p3768-0000-a0.dtb"
KERNEL_ARGS ?= "mminit_loglevel=4 console=ttyTCU0,115200 firmware_class.path=/etc/firmware fbcon=map:0 net.ifnames=0 nospectre_bhb"
MACHINE_EXTRA_RRECOMMENDS += "kernel-module-r8168 kernel-module-r8169 kernel-module-realtek"
MACHINE_EXTRA_RDEPENDS += "linux-firmware-rtl8168"

EMMC_SIZE ?= ""
BOOTPART_SIZE ?= "8388608"
ROOTFSPART_SIZE_DEFAULT ?= "30064771072"
ODMDATA ?= "gbe-uphy-config-8,hsstp-lane-map-3,hsio-uphy-config-0"
EMMC_BCT ?= "tegra234-p3767-0001-sdram-l4t.dts"
NVIDIA_BOARD ?= "t186ref"
TEGRA_AUDIO_DEVICE ?= "tegra-hda-p3767-p3509"

OTABOOTDEV ?= "/dev/mtdblock0"
OTAGPTDEV ?= "/dev/mtdblock0"

TEGRA_PLUGIN_MANAGER_OVERLAYS ?= "tegra234-p3767-overlay.dtbo tegra234-p3768-overlay.dtbo"
