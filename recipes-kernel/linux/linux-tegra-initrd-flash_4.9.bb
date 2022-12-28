require linux-tegra_4.9.bb

KERNEL_PACKAGE_NAME = "initrd-flash-kernel"
INITRAMFS_IMAGE = "${TEGRAFLASH_INITRD_FLASH_IMAGE}"
INITRAMFS_LINK_NAME = "initrd-flash"
PROVIDES:remove = "virtual/kernel"
