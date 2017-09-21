DESCRIPTION = "Minimal initramfs image for Tegra210 platforms with xusb firmware"
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

COMPATIBLE_MACHINE = "(jetsontx1)"

KERNELDEPMODDEPEND = ""

inherit core-image
