# Settings common to all Tegra K1 (tegra124) SoCs

SOC_FAMILY = "tegra124"

require conf/machine/include/soc-family.inc
require conf/machine/include/tune-cortexa15.inc

DEFAULTTUNE = "cortexa15hf-neon"

SOC_FAMILY_PKGARCH = "${ARMPKGARCH}${ARMPKGSFX_DSP}${ARMPKGSFX_EABI}${ARMPKGSFX_ENDIAN}${ARMPKGSFX_FPU}_${SOC_FAMILY}"

PACKAGE_EXTRA_ARCHS_append = " ${SOC_FAMILY_PKGARCH}"

PREFERRED_PROVIDER_virtual/kernel ?= "linux-tegra"
PREFERRED_PROVIDER_u-boot ?= "u-boot-tegra"
PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot-tegra"

KERNEL_IMAGETYPE = "zImage"

MACHINE_FEATURES = "alsa usbhost rtc cuda"
MACHINE_ESSENTIAL_EXTRA_RDEPENDS = "tegra-firmware"
MACHINE_EXTRA_RRECOMMENDS += "kernel-devicetree"

SERIAL_CONSOLE = "115200 ttyS0"

OLDEST_KERNEL = "3.10"

KERNEL_MODULE_AUTOLOAD = ""

CUDA_VERSION ?= "6.5"
CUDA_NVCC_FLAGS ?= "--gpu-architecture=compute_32 --gpu-code=sm_32"
DEB_PKG_EXTENSION = "armhf"

PREFERRED_PROVIDER_virtual/libgl = "tegra-libraries"
PREFERRED_PROVIDER_virtual/egl = "tegra-libraries"
PREFERRED_PROVIDER_virtual/libgles1 = "tegra-libraries"
PREFERRED_PROVIDER_virtual/libgles2 = "tegra-libraries"

PREFERRED_PROVIDER_virtual/xserver = "xserver-xorg"
XSERVER = "xserver-xorg xf86-input-evdev xserver-xorg-video-nvidia xserver-xorg-module-libwfb"

PREFERRED_VERSION_cuda-binaries-native = "${CUDA_VERSION}%"
PREFERRED_VERSION_cuda-shared-binaries-native = "${CUDA_VERSION}%"
PREFERRED_VERSION_cuda-binaries-native = "${CUDA_VERSION}%"
PREFERRED_VERSION_cuda-tools-native = "${CUDA_VERSION}%"
