MACHINEOVERRIDES =. "cuda:tegra:"
SOC_FAMILY_PKGARCH = "${ARMPKGARCH}${ARMPKGSFX_DSP}${ARMPKGSFX_EABI}${ARMPKGSFX_ENDIAN}${ARMPKGSFX_FPU}_${SOC_FAMILY}"
PACKAGE_EXTRA_ARCHS_append = " ${SOC_FAMILY_PKGARCH}"

KERNEL_ROOTSPEC ?= "root=/dev/mmcblk${@uboot_var('devnum')}p${@uboot_var('distro_bootpart')} rw rootwait"
KERNEL_ARGS ??= ""

PREFERRED_PROVIDER_virtual/kernel ?= "linux-tegra"
PREFERRED_PROVIDER_u-boot ?= "u-boot-tegra"
PREFERRED_PROVIDER_virtual/bootloader ?= "u-boot-tegra"

IMAGE_ROOTFS_ALIGNMENT ?= "4"
TEGRA_BLBLOCKSIZE ?= "${@int(d.getVar('IMAGE_ROOTFS_ALIGNMENT')) * 1024}"
EXTRA_IMAGECMD_ext4 ?= "-i 4096 -b 4096"

MACHINE_HWCODECS = "gstreamer1.0-omx-tegra"
LICENSE_FLAGS_WHITELIST_append = " commercial_gstreamer1.0-omx-tegra"

PREFERRED_PROVIDER_virtual/xserver = "xserver-xorg"
XSERVER = "xserver-xorg xf86-input-evdev xserver-xorg-video-nvidia xserver-xorg-module-libwfb"

PREFERRED_VERSION_cuda-binaries-native = "${CUDA_VERSION}%"
PREFERRED_VERSION_cuda-shared-binaries-native = "${CUDA_VERSION}%"
PREFERRED_VERSION_cuda-binaries-native = "${CUDA_VERSION}%"
PREFERRED_VERSION_cuda-tools-native = "${CUDA_VERSION}%"

ROOTFS_POSTPROCESS_COMMAND_append = " remove_mesa_stubs ; "

remove_mesa_stubs() {
	rm -rf ${IMAGE_ROOTFS}${@d.getVar('libdir', False)}/mesa
}
