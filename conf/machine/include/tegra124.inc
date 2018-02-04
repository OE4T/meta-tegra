# Settings common to all Tegra K1 (tegra124) SoCs

SOC_FAMILY = "tegra124"

require conf/machine/include/soc-family.inc
require conf/machine/include/tune-cortexa15.inc

DEFAULTTUNE = "cortexa15hf-neon"

SOC_FAMILY_PKGARCH = "${ARMPKGARCH}${ARMPKGSFX_DSP}${ARMPKGSFX_EABI}${ARMPKGSFX_ENDIAN}${ARMPKGSFX_FPU}_${SOC_FAMILY}"
MACHINEOVERRIDES =. "cuda:"
PACKAGE_EXTRA_ARCHS_append = " ${SOC_FAMILY_PKGARCH}"

PREFERRED_PROVIDER_virtual/kernel ?= "linux-tegra"
PREFERRED_PROVIDER_u-boot ?= "u-boot-tegra"
PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot-tegra"

KERNEL_IMAGETYPE = "zImage"

MACHINE_FEATURES = "alsa usbhost pci rtc cuda"
MACHINE_ESSENTIAL_EXTRA_RDEPENDS = "tegra-firmware kernel-devicetree kernel-image"
MACHINE_EXTRA_RDEPENDS = "tegra-configs-nvstartup tegra-configs-udev"

SERIAL_CONSOLE = "115200 ttyS0"

OLDEST_KERNEL = "3.10"

KERNEL_MODULE_AUTOLOAD = ""

IMAGE_ROOTFS_ALIGNMENT ?= "4"

CUDA_VERSION ?= "6.5"
CUDA_NVCC_ARCH_FLAGS ?= "--gpu-architecture=compute_32 --gpu-code=sm_32"
DEB_PKG_EXTENSION = "armhf"

PREFERRED_PROVIDER_virtual/xserver = "xserver-xorg"
XSERVER = "xserver-xorg xf86-input-evdev xserver-xorg-video-nvidia xserver-xorg-module-libwfb"
PREFERRED_VERSION_xserver-xorg = "1.18.4"

MACHINE_HWCODECS = "gstreamer1.0-omx-tegra"
LICENSE_FLAGS_WHITELIST_append = " commercial_gstreamer1.0-omx-tegra"

PREFERRED_VERSION_cuda-binaries-native = "${CUDA_VERSION}%"
PREFERRED_VERSION_cuda-shared-binaries-native = "${CUDA_VERSION}%"
PREFERRED_VERSION_cuda-binaries-native = "${CUDA_VERSION}%"
PREFERRED_VERSION_cuda-tools-native = "${CUDA_VERSION}%"
