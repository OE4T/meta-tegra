MACHINEOVERRIDES =. "cuda:tegra:"
SOC_FAMILY_PKGARCH = "${ARMPKGARCH}${ARMPKGSFX_DSP}${ARMPKGSFX_EABI}${ARMPKGSFX_ENDIAN}${ARMPKGSFX_FPU}_${SOC_FAMILY}"
TEGRA_PKGARCH = "${ARMPKGARCH}${ARMPKGSFX_DSP}${ARMPKGSFX_EABI}${ARMPKGSFX_ENDIAN}${ARMPKGSFX_FPU}_tegra"
PACKAGE_EXTRA_ARCHS:append = " ${TEGRA_PKGARCH} ${SOC_FAMILY_PKGARCH}"

KERNEL_IMAGETYPES ?= "Image.gz Image"
KERNEL_IMAGETYPE ?= "Image"

KERNEL_ARGS ??= ""

UBOOT_EXTLINUX_KERNEL_IMAGE ?= "/boot/${KERNEL_IMAGETYPE}"
UBOOT_EXTLINUX_KERNEL_ARGS ?= "${KERNEL_ARGS}"
UBOOT_EXTLINUX_INITRD ?= "${@'/boot/initrd' if d.getVar('INITRAMFS_IMAGE') != '' and d.getVar('INITRAMFS_IMAGE_BUNDLE') != '1' else ''}"

NVIDIA_BOARD ?= "generic"

TEGRA_BOOTCONTROL_OVERLAYS ?= "L4TConfiguration.dtbo"
TEGRA_BOOTCONTROL_OVERLAYS += "${@'L4TConfiguration-RootfsRedundancyLevelABEnable.dtbo' if bb.utils.to_boolean(d.getVar('USE_REDUNDANT_FLASH_LAYOUT')) else ''}"
TEGRA_PLUGIN_MANAGER_OVERLAYS ??= ""
TEGRA_DCE_OVERLAYS ??= ""
# The following variable is deprecated; add new overlays
# to one of the above instead.
OVERLAY_DTB_FILE ?= ""

TNSPEC_MACHINE ??= "${MACHINE}"
TNSPEC_BOOTDEV_DEFAULT ??= "mmcblk0p1"
TNSPEC_BOOTDEV ??= "${TNSPEC_BOOTDEV_DEFAULT}"

PREFERRED_PROVIDER_virtual/kernel ?= "linux-jammy-nvidia-tegra"
PREFERRED_PROVIDER_virtual/dtb ?= "nvidia-kernel-oot-dtb"
PREFERRED_PROVIDER_virtual/bootloader ?= "edk2-firmware-tegra"
OPTEE_MM_PROVIDER = "${@'tegra-uefi-prebuilt' if d.getVar('PREFERRED_PROVIDER_virtual/bootloader') == 'tegra-uefi-prebuilt' else 'standalone-mm-optee-tegra'}"
PREFERRED_PROVIDER_standalone-mm-optee-tegra ?= "${OPTEE_MM_PROVIDER}"
LIBGLVND_PROVIDER = "${@'libglvnd' if 'openembedded-layer' in d.getVar('BBFILE_COLLECTIONS').split() else 'libglvnd-tegra'}"
PREFERRED_PROVIDER_libglvnd = "${LIBGLVND_PROVIDER}"
PREFERRED_PROVIDER_virtual/egl = "${LIBGLVND_PROVIDER}"
PREFERRED_PROVIDER_virtual/libgles1 = "${LIBGLVND_PROVIDER}"
PREFERRED_PROVIDER_virtual/libgles2 = "${PREFERRED_PROVIDER_libglvnd}"
PREFERRED_PROVIDER_virtual/libgles3 = "${PREFERRED_PROVIDER_libglvnd}"
PREFERRED_PROVIDER_virtual/libgl = "${PREFERRED_PROVIDER_libglvnd}"
PREFERRED_PROVIDER_libv4l = "${@'v4l-utils' if 'openembedded-layer' in d.getVar('BBFILE_COLLECTIONS').split() else 'libv4l2-minimal'}"
PREFERRED_PROVIDER_v4l-utils = "${@'v4l-utils' if 'openembedded-layer' in d.getVar('BBFILE_COLLECTIONS').split() else 'libv4l2-minimal'}"
PREFERRED_PROVIDER_tensorrt-plugins ?= "tensorrt-plugins-prebuilt"
PREFERRED_PROVIDER_tensorrt-trtexec ?= "tensorrt-trtexec-prebuilt"
PREFERRED_RPROVIDER_tegra-gbm-backend ?= "tegra-libraries-gbm-backend"

USE_PREBUILT_OPTEE ?= "0"

PREFERRED_PROVIDER_virtual/secure-os ?= "${@'tos-prebuilt' if oe.types.boolean(d.getVar('USE_PREBUILT_OPTEE')) else 'tos-optee'}"
PREFERRED_PROVIDER_optee-nvsamples ?= "${@'nvidia-l4t-optee' if oe.types.boolean(d.getVar('USE_PREBUILT_OPTEE')) else 'optee-nvsamples'}"
PREFERRED_PROVIDER_optee-client ?= "${@'nvidia-l4t-optee' if oe.types.boolean(d.getVar('USE_PREBUILT_OPTEE')) else 'optee-client'}"
PREFERRED_PROVIDER_optee-test ?= "${@'nvidia-l4t-optee' if oe.types.boolean(d.getVar('USE_PREBUILT_OPTEE')) else 'optee-test'}"
PREFERRED_RPROVIDER_optee-os ?= "${@'nvidia-l4t-optee-os' if oe.types.boolean(d.getVar('USE_PREBUILT_OPTEE')) else 'optee-os'}"
PREFERRED_RPROVIDER_optee-nvsamples ?= "${@'nvidia-l4t-optee-nvsamples' if oe.types.boolean(d.getVar('USE_PREBUILT_OPTEE')) else 'optee-nvsamples'}"
PREFERRED_RPROVIDER_optee-nvsamples-hwkey-agent ?= "${@'nvidia-l4t-optee-nvsamples-hwkey-agent' if oe.types.boolean(d.getVar('USE_PREBUILT_OPTEE')) else 'optee-nvsamples-hwkey-agent'}"
PREFERRED_RPROVIDER_optee-nvsamples-luks-srv ?= "${@'nvidia-l4t-optee-nvsamples-luks-srv' if oe.types.boolean(d.getVar('USE_PREBUILT_OPTEE')) else 'optee-nvsamples-luks-srv'}"

TEGRA_OPTEE_VERSION ?= "${@'' if oe.types.boolean(d.getVar('USE_PREBUILT_OPTEE')) else '3.22.0-l4t%'}"
PREFERRED_VERSION_optee-os = "${TEGRA_OPTEE_VERSION}"
PREFERRED_VERSION_optee-os-tadevkit = "${TEGRA_OPTEE_VERSION}"
PREFERRED_VERSION_optee-client = "${TEGRA_OPTEE_VERSION}"
PREFERRED_VERSION_optee-test = "${TEGRA_OPTEE_VERSION}"

IMAGE_ROOTFS_ALIGNMENT ?= "4"
TEGRA_BLBLOCKSIZE ?= "${@int(d.getVar('IMAGE_ROOTFS_ALIGNMENT')) * 1024}"
EXTRA_IMAGECMD:ext4 ?= "-i 4096 -b 4096"
IMAGE_CLASSES += "image_types_tegra"
IMAGE_FSTYPES += "tegraflash"

INITRAMFS_IMAGE ?= "tegra-minimal-initramfs"
INITRAMFS_IMAGE_BUNDLE ?= "0"
TEGRA_INITRAMFS_FSTYPES ?= "${@'' if d.getVar('INITRAMFS_IMAGE_BUNDLE') == '1' else ' cpio.gz.cboot'}"
INITRAMFS_FSTYPES:append = "${TEGRA_INITRAMFS_FSTYPES}"
TEGRA_INITRD_FLASH_INITRAMFS_FSTYPES ?= "${@'' if d.getVar('INITRAMFS_IMAGE_BUNDLE') == '1' else ' cpio.gz.cboot'}"
TEGRAFLASH_INITRD_FLASH_IMAGE ?= "tegra-initrd-flash-initramfs"

TEGRAFLASH_NO_INTERNAL_STORAGE ?= "0"

UBOOT_EXTLINUX ?= "1"
TEGRA_ESSENTIAL_EXTRA_RDEPENDS ?= "${@'l4t-launcher-extlinux' if d.getVar('UBOOT_EXTLINUX') == '1' else ''}"
MACHINE_FEATURES = "alsa usbhost pci rtc cuda ext2"
MACHINE_ESSENTIAL_EXTRA_RDEPENDS = "tegra-firmware ${TEGRA_ESSENTIAL_EXTRA_RDEPENDS} nvidia-kernel-oot-base"
MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS = "kernel-module-ina3221 kernel-module-lm90 kernel-module-tegra-bpmp-thermal kernel-module-spi-tegra114 kernel-module-pwm-fan \
                                       kernel-module-governor-userspace kernel-module-ucsi-ccg kernel-module-pcie-tegra194 kernel-module-phy-tegra194-p2u \
                                       kernel-module-nvme kernel-module-pwm-tegra kernel-module-tegra-xudc"
MACHINE_EXTRA_RDEPENDS = "tegra-nvphs tegra-nvs-service tegra-nvsciipc tegra-nvstartup tegra-nvfancontrol tegra-configs-udev tegra-redundant-boot nvidia-kernel-oot-display"
MACHINE_EXTRA_RRECOMMENDS = "kernel-module-snd-hda-tegra kernel-module-snd-hda-codec-hdmi nvidia-kernel-oot-cameras nvidia-kernel-oot-canbus"
MACHINE_HWCODECS = "gstreamer1.0-plugins-nvvideo4linux2"

PREFERRED_PROVIDER_virtual/xserver ?= "xserver-xorg"
PREFERRED_PROVIDER_virtual/xserver-xf86 ?= "xserver-xorg"
XSERVER ?= "xserver-xorg xf86-input-evdev xserver-xorg-video-nvidia xserver-xorg-module-libwfb"

PREFERRED_RPROVIDER_kernel-module-host1x ?= "nv-kernel-module-host1x"
PREFERRED_RPROVIDER_kernel-module-snd-soc-tegra186-asrc ?= "nv-kernel-module-snd-soc-tegra186-asrc"
PREFERRED_RPROVIDER_kernel-module-snd-soc-tegra186-dspk ?= "nv-kernel-module-snd-soc-tegra186-dspk"
PREFERRED_RPROVIDER_kernel-module-snd-soc-tegra210-admaif ?= "nv-kernel-module-snd-soc-tegra210-admaif"
PREFERRED_RPROVIDER_kernel-module-snd-soc-tegra210-adx ?= "nv-kernel-module-snd-soc-tegra210-adx"
PREFERRED_RPROVIDER_kernel-module-snd-soc-tegra210-ahub ?= "nv-kernel-module-snd-soc-tegra210-ahub"
PREFERRED_RPROVIDER_kernel-module-snd-soc-tegra210-amx ?= "nv-kernel-module-snd-soc-tegra210-amx"
PREFERRED_RPROVIDER_kernel-module-snd-soc-tegra210-dmic ?= "nv-kernel-module-snd-soc-tegra210-dmic"
PREFERRED_RPROVIDER_kernel-module-snd-soc-tegra210-i2s ?= "nv-kernel-module-snd-soc-tegra210-i2s"
PREFERRED_RPROVIDER_kernel-module-snd-soc-tegra210-mixer ?= "nv-kernel-module-snd-soc-tegra210-mixer"
PREFERRED_RPROVIDER_kernel-module-snd-soc-tegra210-mvc ?= "nv-kernel-module-snd-soc-tegra210-mvc"
PREFERRED_RPROVIDER_kernel-module-snd-soc-tegra210-ope ?= "nv-kernel-module-snd-soc-tegra210-ope"
PREFERRED_RPROVIDER_kernel-module-snd-soc-tegra210-sfc ?= "nv-kernel-module-snd-soc-tegra210-sfc"
PREFERRED_RPROVIDER_kernel-module-tegra-bpmp-thermal ?= "nv-kernel-module-tegra-bpmp-thermal"
PREFERRED_RPROVIDER_kernel-module-tegra-drm ?= "nv-kernel-module-tegra-drm"

CUDA_VERSION ?= "12.2"
CUDA_ARCHITECTURES ?= "${TEGRA_CUDA_ARCHITECTURE}"
CUDA_NVCC_ARCH_FLAGS ?= "--gpu-architecture=compute_${TEGRA_CUDA_ARCHITECTURE} --gpu-code=sm_${TEGRA_CUDA_ARCHITECTURE}"

L4T_BSP_ARCH = "tegra"
L4T_BSP_PKGARCH = "${TEGRA_PKGARCH}"

EFI_PROVIDER ?= "l4t-launcher"
TEGRA_SIGNING_ENV ?= "CHIPREV=${TEGRA_CHIPREV} BOARDID=${TEGRA_BOARDID} FAB=${TEGRA_FAB} BOARDSKU=${TEGRA_BOARDSKU} BOARDREV=${TEGRA_BOARDREV}"

TEGRA_FLASHVARS ?= "BPFDTB_FILE BPF_FILE BR_CMD_CONFIG DEVICEPROD_CONFIG DEVICE_CONFIG DEV_PARAMS DEV_PARAMS_B EMC_FUSE_DEV_PARAMS EMMC_CFG GPIOINT_CONFIG MB2BCT_CFG MINRATCHET_CONFIG MISC_COLD_BOOT_CONFIG MISC_CONFIG PINMUX_CONFIG PMC_CONFIG PMIC_CONFIG PROD_CONFIG SCR_COLD_BOOT_CONFIG SCR_CONFIG TBCDTB_FILE UPHY_CONFIG WB0SDRAM_BCT"
TEGRA_FLASHVAR_TBCDTB_FILE ?= "@DTBFILE@"

EMMC_BCTS ?= "${EMMC_BCT}${@',' + d.getVar('EMMC_BCT_OVERRIDE') if d.getVar('EMMC_BCT_OVERRIDE') else ''}"

TEGRA_UEFI_SIGNING_CLASS ?= "tegra-uefi-signing"

USE_REDUNDANT_FLASH_LAYOUT_DEFAULT ??= "0"
HAS_REDUNDANT_PARTITION_LAYOUT_EXTERNAL ?= "1"
USE_REDUNDANT_FLASH_LAYOUT ?= "${@d.getVar('USE_REDUNDANT_FLASH_LAYOUT_DEFAULT') if bb.utils.to_boolean(d.getVar('HAS_REDUNDANT_PARTITION_LAYOUT_EXTERNAL')) else '0'}"
PARTITION_LAYOUT_TEMPLATE_DEFAULT_SUPPORTS_REDUNDANT ??= "0"

PARTITION_LAYOUT_TEMPLATE_REDUNDANT ?= "${@d.getVar('PARTITION_LAYOUT_TEMPLATE_DEFAULT') if bb.utils.to_boolean(d.getVar('PARTITION_LAYOUT_TEMPLATE_DEFAULT_SUPPORTS_REDUNDANT')) else d.getVar('PARTITION_LAYOUT_TEMPLATE_DEFAULT').replace('.xml','_rootfs_ab.xml')}"
PARTITION_LAYOUT_TEMPLATE ?= "${@d.getVar('PARTITION_LAYOUT_TEMPLATE_REDUNDANT') if bb.utils.to_boolean(d.getVar('USE_REDUNDANT_FLASH_LAYOUT')) else d.getVar('PARTITION_LAYOUT_TEMPLATE_DEFAULT')}"
PARTITION_LAYOUT_EXTERNAL_REDUNDANT ?= "${@d.getVar('PARTITION_LAYOUT_EXTERNAL_DEFAULT').replace('.xml','_rootfs_ab.xml')}"
PARTITION_LAYOUT_EXTERNAL ?= "${@d.getVar('PARTITION_LAYOUT_EXTERNAL_REDUNDANT') if bb.utils.to_boolean(d.getVar('USE_REDUNDANT_FLASH_LAYOUT')) else d.getVar('PARTITION_LAYOUT_EXTERNAL_DEFAULT')}"

ROOTFSPART_SIZE_REDUNDANT ?= "${@int(d.getVar('ROOTFSPART_SIZE_DEFAULT')) // 2}"
ROOTFSPART_SIZE ?= "${@d.getVar('ROOTFSPART_SIZE_REDUNDANT') if bb.utils.to_boolean(d.getVar('USE_REDUNDANT_FLASH_LAYOUT')) else d.getVar('ROOTFSPART_SIZE_DEFAULT')}"
