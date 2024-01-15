FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

# ostree support
SRC_URI:append:sota = " \
        file://0001-L4TLauncher-boot-syslinux-instead-of-extlinux-for-os.patch;patchdir=../edk2-nvidia \
        file://0002-l4tlauncher-support-booting-otaroot-based-partitions.patch;patchdir=../edk2-nvidia \
        file://0003-l4tlauncher-disable-a-b-rootfs-validation.patch;patchdir=../edk2-nvidia \
"
