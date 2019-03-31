# Settings common to all Xavier (tegra194) SoCs

SOC_FAMILY = "tegra194"

require conf/machine/include/soc-family.inc
require conf/machine/include/arm/arch-armv8a.inc

KERNEL_IMAGETYPE = "Image"

MACHINE_FEATURES = "alsa usbhost pci rtc cuda"
MACHINE_ESSENTIAL_EXTRA_RDEPENDS = "tegra-firmware kernel-devicetree kernel-image"
MACHINE_EXTRA_RDEPENDS = "tegra-nvpmodel tegra-nvphs tegra-nvs-service tegra-configs-nvstartup tegra-configs-udev kernel-module-nvgpu kernel-module-nvs"

INITRD_IMAGE ?=	"tegra-minimal-initramfs"
IMAGE_CLASSES_append = " image_types_cboot"
INITRD_FSTYPES ?= "cpio.gz.cboot cpio.gz.cboot.bup-payload"
IMAGE_UBOOT ?= ""

TEGRA_AUDIO_DEVICE ?= "tegrahdat194ref"

SERIAL_CONSOLE = "115200 ttyTCU0"

KERNEL_MODULE_AUTOLOAD = "nvgpu"

NVIDIA_CHIP = "0x19"

TEGRA_CHIPREV ?= "2"
TEGRA_FAB ?= "400"
TEGRA_BOARDID ?= "2888"

DEB_PKG_EXTENSION = "arm64"

CUDA_VERSION ?= "10.0"
CUDA_NVCC_ARCH_FLAGS ?= "--gpu-architecture=compute_72 --gpu-code=sm_72"

PREFERRED_PROVIDER_libdrm = "libdrm-tegra-shim"
PREFERRED_PROVIDER_drm = "libdrm-tegra-shim"
PREFERRED_PROVIDER_virtual/bootloader ?= "cboot-prebuilt"
PREFERRED_PROVIDER_cboot ?= "cboot-prebuilt"

require conf/machine/include/tegra-common.inc

L4T_BSP_NAME = "jax-tx2"
L4T_BSP_BASEPFX = "JAX-TX2"
L4T_BSP_PREFIX = "${L4T_BSP_BASEPFX}-Jetson"
