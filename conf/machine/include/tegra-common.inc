MACHINEOVERRIDES =. "cuda:tegra:"
SOC_FAMILY_PKGARCH = "${ARMPKGARCH}${ARMPKGSFX_DSP}${ARMPKGSFX_EABI}${ARMPKGSFX_ENDIAN}${ARMPKGSFX_FPU}_${SOC_FAMILY}"
TEGRA_PKGARCH = "${ARMPKGARCH}${ARMPKGSFX_DSP}${ARMPKGSFX_EABI}${ARMPKGSFX_ENDIAN}${ARMPKGSFX_FPU}_tegra"
PACKAGE_EXTRA_ARCHS_append = " ${TEGRA_PKGARCH} ${SOC_FAMILY_PKGARCH}"

KERNEL_IMAGETYPES ?= "Image.gz Image"
KERNEL_IMAGETYPE ?= "Image.gz"

KERNEL_ROOTSPEC ?= "root=/dev/mmcblk${devnum}p${distro_bootpart} rw rootwait"
KERNEL_ARGS ??= ""

UBOOT_EXTLINUX ?= "1"
UBOOT_EXTLINUX_FDTDIR ?= ""
UBOOT_EXTLINUX_ROOT ?= "${cbootargs} ${KERNEL_ROOTSPEC}"
# Console setting comes from ${cbootargs}
UBOOT_EXTLINUX_CONSOLE = ""
UBOOT_EXTLINUX_KERNEL_ARGS ?= "${bootargs}"
UBOOT_EXTLINUX_INITRD ?= "${@'../initrd' if d.getVar('INITRAMFS_IMAGE') != '' and d.getVar('INITRAMFS_IMAGE_BUNDLE') != '1' else ''}"

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
PREFERRED_PROVIDER_virtual/bootlogo ?= "bootlogo-prebuilt"

IMAGE_ROOTFS_ALIGNMENT ?= "4"
TEGRA_BLBLOCKSIZE ?= "${@int(d.getVar('IMAGE_ROOTFS_ALIGNMENT')) * 1024}"
EXTRA_IMAGECMD_ext4 ?= "-i 4096 -b 4096"
IMAGE_CLASSES += "image_types_tegra"
IMAGE_FSTYPES ?= "tegraflash"

TEGRA_ESSENTIAL_EXTRA_RDEPENDS ??= ""
MACHINE_FEATURES = "alsa usbhost pci rtc cuda ext2"
MACHINE_ESSENTIAL_EXTRA_RDEPENDS = "tegra-firmware ${TEGRA_ESSENTIAL_EXTRA_RDEPENDS}"
MACHINE_EXTRA_RDEPENDS = "tegra-nvpmodel tegra-nvphs tegra-nvs-service tegra-nvstartup tegra-configs-udev"
MACHINE_EXTRA_RRECOMMENDS = "kernel-module-nvgpu kernel-module-tegra-udrm"
MACHINE_HWCODECS = "gstreamer1.0-omx-tegra gstreamer1.0-plugins-nvvideo4linux2"

PREFERRED_PROVIDER_virtual/xserver = "xserver-xorg"
XSERVER = "xserver-xorg xf86-input-evdev xserver-xorg-video-nvidia xserver-xorg-module-libwfb"

CUDA_VERSION ?= "10.2"
# Tegra specific version of libnvidia-container
PREFERRED_VERSION_libnvidia-container-tools = "0.10.0"

L4T_BSP_ARCH = "tegra"
L4T_BSP_PKGARCH = "${TEGRA_PKGARCH}"

SMDFILE ?= "slot_metadata.bin"
