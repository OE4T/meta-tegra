DESCRIPTION = "Minimal initramfs image for Tegra platforms"
LICENSE = "MIT"

TEGRA_INITRD_INSTALL ??= ""

TEGRA_INITRD_BASEUTILS ?= "busybox"

PACKAGE_INSTALL = "\
    tegra-firmware-xusb \
    tegra-minimal-init \
    ${TEGRA_INITRD_BASEUTILS} \
    ${ROOTFS_BOOTSTRAP_INSTALL} \
    ${TEGRA_INITRD_INSTALL} \
"

IMAGE_FEATURES = ""
IMAGE_LINGUAS = ""

COPY_LIC_MANIFEST = "0"
COPY_LIC_DIRS = "0"

COMPATIBLE_MACHINE = "(tegra)"

KERNELDEPMODDEPEND = ""

IMAGE_ROOTFS_SIZE = "32768"
IMAGE_ROOTFS_EXTRA_SPACE = "0"

FORCE_RO_REMOVE ?= "1"

inherit core-image

IMAGE_FSTYPES = "${INITRAMFS_FSTYPES}"

SSTATE_SKIP_CREATION_task-image-complete = "0"
SSTATE_SKIP_CREATION_task-image-qa = "0"
do_image_complete[vardepsexclude] += "rm_work_rootfs"
IMAGE_POSTPROCESS_COMMAND = ""

inherit nopackages
