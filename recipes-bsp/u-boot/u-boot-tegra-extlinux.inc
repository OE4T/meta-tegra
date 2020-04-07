KERNEL_ROOTSPEC ??= ""
KERNEL_ARGS ??= ""

def uboot_var(name):
    return '${' + name + '}'

UBOOT_EXTLINUX_tegra = "1"
UBOOT_EXTLINUX_LABELS_tegra = "primary"
UBOOT_EXTLINUX_KERNEL_IMAGE_primary_tegra = "../${KERNEL_IMAGETYPE}"
UBOOT_EXTLINUX_FDTDIR_primary_tegra = ""
UBOOT_EXTLINUX_FDT_primary_tegra = ""
UBOOT_EXTLINUX_ROOT_primary_tegra = "${@uboot_var('cbootargs')} ${KERNEL_ROOTSPEC}"
# Console setting comes from ${cbootargs}
UBOOT_EXTLINUX_CONSOLE_tegra = ""
UBOOT_EXTLINUX_KERNEL_ARGS_primary_tegra = "${@uboot_var('bootargs')}"
UBOOT_EXTLINUX_INITRD_primary_tegra = "${@'../initrd' if d.getVar('INITRAMFS_IMAGE') != '' and d.getVar('TEGRA_INITRAMFS_INITRD') == '1' else ''}"
