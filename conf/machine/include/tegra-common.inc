MACHINEOVERRIDES =. "cuda:tegra:"
SOC_FAMILY_PKGARCH = "${ARMPKGARCH}${ARMPKGSFX_DSP}${ARMPKGSFX_EABI}${ARMPKGSFX_ENDIAN}${ARMPKGSFX_FPU}_${SOC_FAMILY}"
TEGRA_PKGARCH = "${ARMPKGARCH}${ARMPKGSFX_DSP}${ARMPKGSFX_EABI}${ARMPKGSFX_ENDIAN}${ARMPKGSFX_FPU}_tegra"
PACKAGE_EXTRA_ARCHS:append = " ${TEGRA_PKGARCH} ${SOC_FAMILY_PKGARCH}"

KERNEL_IMAGETYPES ?= "Image.gz Image"
KERNEL_IMAGETYPE ?= "Image"

KERNEL_ROOTSPEC ?= "root=/dev/mmcblk0p1 rootfstype=ext4 rw rootwait"
KERNEL_ARGS ??= ""

UBOOT_EXTLINUX_FDTDIR ?= ""
UBOOT_EXTLINUX_ROOT ?= "${KERNEL_ROOTSPEC}"
UBOOT_EXTLINUX_KERNEL_IMAGE ?= "/boot/${KERNEL_IMAGETYPE}"
UBOOT_EXTLINUX_INITRD ?= "${@'/boot/initrd' if d.getVar('INITRAMFS_IMAGE') != '' and d.getVar('INITRAMFS_IMAGE_BUNDLE') != '1' else ''}"
UBOOT_EXTLINUX_KERNEL_ARGS ?= "${KERNEL_ARGS}"

TNSPEC_BOOTDEV ??= "mmcblk0p1"
TNSPEC ?= "${TEGRA_BOARDID}-${TEGRA_FAB}-${TEGRA_BOARDSKU}-${TEGRA_BOARDREV}-1-${TEGRA_CHIPREV}-${MACHINE}-${TNSPEC_BOOTDEV}"

PREFERRED_PROVIDER_virtual/kernel ?= "linux-tegra"
PREFERRED_PROVIDER_virtual/bootloader ?= "tegra-uefi-prebuilt"
PREFERRED_PROVIDER_virtual/secure-os ?= "tos-prebuilt"
PREFERRED_PROVIDER_virtual/egl = "libglvnd"
PREFERRED_PROVIDER_virtual/libgles1 = "libglvnd"
PREFERRED_PROVIDER_virtual/libgles2 = "libglvnd"
PREFERRED_PROVIDER_virtual/libgles3 = "libglvnd"
PREFERRED_PROVIDER_virtual/libgl = "libglvnd"
PREFERRED_PROVIDER_libv4l = "${@'v4l-utils' if 'openembedded-layer' in d.getVar('BBFILE_COLLECTIONS').split() else 'libv4l2-minimal'}"
PREFERRED_PROVIDER_v4l-utils = "${@'v4l-utils' if 'openembedded-layer' in d.getVar('BBFILE_COLLECTIONS').split() else 'libv4l2-minimal'}"
PREFERRED_PROVIDER_virtual/bootlogo ?= "bootlogo-prebuilt"
PREFERRED_PROVIDER_tensorrt-plugins ?= "tensorrt-plugins-prebuilt"
PREFERRED_PROVIDER_tensorrt-trtexec ?= "tensorrt-trtexec-prebuilt"

IMAGE_ROOTFS_ALIGNMENT ?= "4"
TEGRA_BLBLOCKSIZE ?= "${@int(d.getVar('IMAGE_ROOTFS_ALIGNMENT')) * 1024}"
EXTRA_IMAGECMD:ext4 ?= "-i 4096 -b 4096"
IMAGE_CLASSES += "image_types_tegra"
IMAGE_FSTYPES ?= "tegraflash"

TEGRA_ESSENTIAL_EXTRA_RDEPENDS ??= ""
MACHINE_FEATURES = "alsa usbhost pci rtc cuda ext2"
MACHINE_ESSENTIAL_EXTRA_RDEPENDS = "tegra-firmware ${TEGRA_ESSENTIAL_EXTRA_RDEPENDS}"
MACHINE_EXTRA_RDEPENDS = "tegra-nvphs tegra-nvs-service tegra-nvsciipc tegra-nvstartup tegra-nvfancontrol tegra-configs-udev"
MACHINE_EXTRA_RRECOMMENDS = "kernel-module-nvgpu kernel-module-tegra-udrm \
			   kernel-module-snd-hda-tegra kernel-module-snd-hda-codec-hdmi kernel-module-ina3221 \
                           kernel-module-tegra-bpmp-thermal kernel-module-spi-tegra114 kernel-module-ahci \
                           kernel-module-max77620-thermal kernel-module-pwm-fan"
MACHINE_HWCODECS = "gstreamer1.0-plugins-nvvideo4linux2"

PREFERRED_PROVIDER_virtual/xserver ?= "xserver-xorg-tegra"
PREFERRED_PROVIDER_virtual/xserver-xf86 ?= "xserver-xorg-tegra"
XSERVER ?= "xserver-xorg-tegra xf86-input-evdev xserver-xorg-video-nvidia xserver-xorg-tegra-module-libwfb"

CUDA_VERSION ?= "11.4"
CUDA_ARCHITECTURES ?= "${TEGRA_CUDA_ARCHITECTURE}"
CUDA_NVCC_ARCH_FLAGS ?= "--gpu-architecture=compute_${TEGRA_CUDA_ARCHITECTURE} --gpu-code=sm_${TEGRA_CUDA_ARCHITECTURE}"
# Tegra specific version of libnvidia-container
PREFERRED_VERSION_libnvidia-container-tools = "0.10.0"

L4T_BSP_ARCH = "tegra"
L4T_BSP_PKGARCH = "${TEGRA_PKGARCH}"

SMDFILE ?= "slot_metadata.bin"
