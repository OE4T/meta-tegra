# Settings common to all Tegra X1 (tegra210) SoCs

SOC_FAMILY = "tegra210"

require conf/machine/include/soc-family.inc
require conf/machine/include/arm/arch-armv8.inc

SOC_FAMILY_PKGARCH = "${ARMPKGARCH}${ARMPKGSFX_DSP}${ARMPKGSFX_EABI}${ARMPKGSFX_ENDIAN}${ARMPKGSFX_FPU}_${SOC_FAMILY}"

PACKAGE_EXTRA_ARCHS_append = " ${SOC_FAMILY_PKGARCH}"

PREFERRED_PROVIDER_virtual/kernel ?= "linux-tegra210"
PREFERRED_PROVIDER_u-boot ?= "u-boot-tegra"
PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot-tegra"

KERNEL_IMAGETYPE = "Image"

MACHINE_FEATURES = "alsa bluetooth usbhost pci wifi rtc cuda"
MACHINE_ESSENTIAL_EXTRA_RDEPENDS = "tegra-firmware"
MACHINE_EXTRA_RRECOMMENDS += "kernel-devicetree tegra-firmware-brcm udev-rules-tegra kernel-module-bcmdhd"

INITRAMFS_IMAGE ?= "tegra210-minimal-initramfs"
INITRAMFS_TASK = "${@['', '${INITRAMFS_IMAGE}:do_image_complete'][(d.getVar('INITRAMFS_IMAGE', True) or '') != '']}"

SERIAL_CONSOLE = "115200 ttyS0"

OLDEST_KERNEL_aarch64 = "3.10"

PREFERRED_PROVIDER_virtual/libgl = "tegra-libraries"
PREFERRED_PROVIDER_virtual/egl = "tegra-libraries"
PREFERRED_PROVIDER_virtual/libgles1 = "tegra-libraries"
PREFERRED_PROVIDER_virtual/libgles2 = "tegra-libraries"

PREFERRED_PROVIDER_virtual/xserver = "xserver-xorg"
XSERVER = "xserver-xorg xf86-input-evdev xserver-xorg-video-nvidia xserver-xorg-module-libwfb"
MACHINE_HWCODECS = "gstreamer1.0-omx-tegra"
LICENSE_FLAGS_WHITELIST_append = " commercial_gstreamer1.0-omx-tegra"

KERNEL_MODULE_AUTOLOAD = ""

NVIDIA_CHIP = "0x21"

DEB_PKG_EXTENSION = "arm64"

CUDA_VERSION ?= "8.0"
CUDA_NVCC_FLAGS ?= "--gpu-architecture=compute_53 --gpu=code=sm_53"

PREFERRED_VERSION_cuda-binaries-native = "${CUDA_VERSION}%"
PREFERRED_VERSION_cuda-shared-binaries-native = "${CUDA_VERSION}%"
PREFERRED_VERSION_cuda-binaries-native = "${CUDA_VERSION}%"
PREFERRED_VERSION_cuda-tools-native = "${CUDA_VERSION}%"
