DESCRIPTION = "Minimal initramfs image for Tegra186 platforms"
LICENSE = "MIT"

PACKAGE_INSTALL = "\
    tegra-firmware-xusb \
    tegra210-minimal-init \
"

IMAGE_FEATURES = ""
IMAGE_LINGUAS = ""
IMAGE_FSTYPES = "${INITRAMFS_FSTYPES}"

COPY_LIC_MANIFEST = "0"
COPY_LIC_DIRS = "0"

COMPATIBLE_MACHINE = "(tegra186)"

KERNELDEPMODDEPEND = ""

IMAGE_ROOTFS_SIZE = "8192"

inherit core-image
