MACHINEOVERRIDES =. "cuda:tegra:"
SOC_FAMILY_PKGARCH = "${ARMPKGARCH}${ARMPKGSFX_DSP}${ARMPKGSFX_EABI}${ARMPKGSFX_ENDIAN}${ARMPKGSFX_FPU}_${SOC_FAMILY}"
PACKAGE_EXTRA_ARCHS_append = " ${SOC_FAMILY_PKGARCH}"

KERNEL_ROOTSPEC ?= "root=/dev/mmcblk${@uboot_var('devnum')}p${@uboot_var('distro_bootpart')} rw rootwait"
KERNEL_ARGS ??= ""

TNSPEC_BOOTDEV ??= "mmcblk0p1"
TNSPEC ?= "${TEGRA_BOARDID}-${TEGRA_FAB}-${TEGRA_BOARDSKU}-${TEGRA_BOARDREV}-1-${TEGRA_CHIPREV}-${MACHINE}-${TNSPEC_BOOTDEV}"

PREFERRED_PROVIDER_virtual/kernel ?= "linux-tegra"
PREFERRED_PROVIDER_u-boot ?= "u-boot-tegra"
PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot-tegra"
PREFERRED_PROVIDER_cboot ?= "cboot-prebuilt"
PREFERRED_PROVIDER_virtual/secure-os ?= "tos-prebuilt"
PREFERRED_PROVIDER_virtual/egl = "libglvnd"
PREFERRED_PROVIDER_virtual/libgles1 = "libglvnd"
PREFERRED_PROVIDER_virtual/libgles2 = "libglvnd"
PREFERRED_PROVIDER_virtual/libgl = "libglvnd"
PREFERRED_PROVIDER_libv4l = "${@'v4l-utils' if 'openembedded-layer' in d.getVar('BBFILE_COLLECTIONS').split() else 'libv4l2-minimal'}"
PREFERRED_PROVIDER_v4l-utils = "${@'v4l-utils' if 'openembedded-layer' in d.getVar('BBFILE_COLLECTIONS').split() else 'libv4l2-minimal'}"

IMAGE_ROOTFS_ALIGNMENT ?= "4"
TEGRA_BLBLOCKSIZE ?= "${@int(d.getVar('IMAGE_ROOTFS_ALIGNMENT')) * 1024}"
EXTRA_IMAGECMD_ext4 ?= "-i 4096 -b 4096"
IMAGE_CLASSES += "image_types_tegra"
IMAGE_FSTYPES ?= "tegraflash"

MACHINE_HWCODECS = "gstreamer1.0-omx-tegra"
LICENSE_FLAGS_WHITELIST_append = " commercial_gstreamer1.0-omx-tegra"

PREFERRED_PROVIDER_virtual/xserver = "xserver-xorg"
XSERVER = "xserver-xorg xf86-input-evdev xserver-xorg-video-nvidia xserver-xorg-module-libwfb"

PREFERRED_VERSION_cuda-binaries-native = "${CUDA_VERSION}%"
PREFERRED_VERSION_cuda-shared-binaries-native = "${CUDA_VERSION}%"
PREFERRED_VERSION_cuda-binaries-native = "${CUDA_VERSION}%"
PREFERRED_VERSION_cuda-toolkit-native = "${CUDA_VERSION}%"
# Tegra specific version of libnvidia-container
PREFERRED_VERSION_libnvidia-container-tools = "0.9.0"
