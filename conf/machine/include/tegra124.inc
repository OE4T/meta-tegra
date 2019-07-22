# Settings common to all Tegra K1 (tegra124) SoCs

SOC_FAMILY = "tegra124"

DEFAULTTUNE ?= "cortexa15thf-neon-vfpv4"

require conf/machine/include/soc-family.inc
require conf/machine/include/tune-cortexa15.inc

KERNEL_IMAGETYPE = "zImage"
KERNEL_ROOTSPEC ?= "root=/dev/mmcblk0p1 rw rootwait"

MACHINE_FEATURES = "alsa usbhost pci rtc cuda"
MACHINE_ESSENTIAL_EXTRA_RDEPENDS = "tegra-firmware kernel-devicetree kernel-image u-boot"
MACHINE_EXTRA_RDEPENDS = "tegra-configs-nvstartup tegra-configs-udev"

SERIAL_CONSOLE = "115200 ttyS0"

OLDEST_KERNEL = "3.10"

KERNEL_MODULE_AUTOLOAD = ""

CUDA_VERSION ?= "6.5"
CUDA_NVCC_ARCH_FLAGS ?= "--gpu-architecture=compute_32 --gpu-code=sm_32"
DEB_PKG_EXTENSION = "armhf"

PREFERRED_VERSION_xserver-xorg = "1.18.4"

require conf/machine/include/tegra-common.inc
