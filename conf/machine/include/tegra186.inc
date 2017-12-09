# Settings common to all Tegra X2 (tegra186) SoCs

SOC_FAMILY = "tegra186"

require conf/machine/include/soc-family.inc
require conf/machine/include/arm/arch-armv8.inc

SOC_FAMILY_PKGARCH = "${ARMPKGARCH}${ARMPKGSFX_DSP}${ARMPKGSFX_EABI}${ARMPKGSFX_ENDIAN}${ARMPKGSFX_FPU}_${SOC_FAMILY}"
MACHINEOVERRIDES =. "cuda:"
PACKAGE_EXTRA_ARCHS_append = " ${SOC_FAMILY_PKGARCH}"

PREFERRED_PROVIDER_virtual/kernel ?= "linux-tegra"
PREFERRED_PROVIDER_u-boot ?= "u-boot-tegra"
PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot-tegra"

KERNEL_IMAGETYPE = "Image"

MACHINE_FEATURES = "alsa bluetooth usbhost pci wifi rtc cuda"
MACHINE_ESSENTIAL_EXTRA_RDEPENDS = "tegra-firmware kernel-devicetree kernel-image"
MACHINE_EXTRA_RDEPENDS = "tegra-tools-nvpmodel tegra-configs-nvstartup tegra-configs-udev"
MACHINE_EXTRA_RRECOMMENDS += "tegra-firmware-brcm kernel-module-bcmdhd"

INITRAMFS_IMAGE ?= "tegra186-minimal-initramfs"
TEGRA_INITRAMFS_INITRD ?= "1"

SERIAL_CONSOLE = "115200 ttyS0"

PREFERRED_PROVIDER_virtual/xserver = "xserver-xorg"
XSERVER = "xserver-xorg xf86-input-evdev xserver-xorg-video-nvidia xserver-xorg-module-libwfb"
MACHINE_HWCODECS = "gstreamer1.0-omx-tegra"
LICENSE_FLAGS_WHITELIST_append = " commercial_gstreamer1.0-omx-tegra"

KERNEL_MODULE_AUTOLOAD = ""

IMAGE_ROOTFS_ALIGNMENT ?= "4"
EXTRA_IMAGECMD_ext4 ?= "-i 4096 -b 4096"

NVIDIA_CHIP = "0x18"

DEB_PKG_EXTENSION = "arm64"

CUDA_VERSION ?= "9.0"
CUDA_NVCC_ARCH_FLAGS ?= "--gpu-architecture=compute_61 --gpu-code=sm_61"

PREFERRED_VERSION_cuda-binaries-native = "${CUDA_VERSION}%"
PREFERRED_VERSION_cuda-shared-binaries-native = "${CUDA_VERSION}%"
PREFERRED_VERSION_cuda-binaries-native = "${CUDA_VERSION}%"
PREFERRED_VERSION_cuda-tools-native = "${CUDA_VERSION}%"
