DESCRIPTION = "Minimal initramfs image for initrd flashing"
LICENSE = "MIT"

TEGRA_INITRD_FLASH_INSTALL ??= ""

TEGRA_INITRD_FLASH_BASEUTILS ?= "busybox"

PACKAGE_INSTALL = "\
    tegra-firmware-xusb \
    tegra-flash-init \
    ${TEGRA_INITRD_FLASH_BASEUTILS} \
    ${ROOTFS_BOOTSTRAP_INSTALL} \
    ${TEGRA_INITRD_FLASH_INSTALL} \
"

# Cannot use Image.gz for RCM booting
KERNEL_IMAGETYPE = "Image"

IMAGE_FEATURES = ""
IMAGE_LINGUAS = ""

COPY_LIC_MANIFEST = "0"
COPY_LIC_DIRS = "0"

COMPATIBLE_MACHINE = "(tegra)"

IMAGE_ROOTFS_SIZE = "32768"
IMAGE_ROOTFS_EXTRA_SPACE = "0"

FORCE_RO_REMOVE ?= "1"

inherit core-image

# For U-boot platforms we normally don't generate a cboot (Android-style)
# image, but we need one for initrd flashing.
INITRAMFS_FSTYPES:append:tegra186 = "${@' cpio.gz.cboot' if not d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('cboot') else ''}"
INITRAMFS_FSTYPES:append:tegra210 = "${@' cpio.gz.cboot' if not d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('cboot') else ''}"
# But make sure we don't generate a BUP payload for this
INITRAMFS_FSTYPES:remove = "cpio.gz.cboot.bup-payload"
IMAGE_FSTYPES = "${INITRAMFS_FSTYPES}"

SSTATE_SKIP_CREATION:task-image-complete = "0"
SSTATE_SKIP_CREATION:task-image-qa = "0"
do_image_complete[vardepsexclude] += "rm_work_rootfs"
IMAGE_POSTPROCESS_COMMAND = ""
inherit nopackages
# XXX
# Temporarily override this function from sstate.bbclass
# until a better solution is found.
# XXX
python sstate_report_unihash() {
    report_unihash = getattr(bb.parse.siggen, 'report_unihash', None)

    if report_unihash:
        ss = sstate_state_fromvars(d)
        if ss['task'] == 'image_complete':
            os.environ['PSEUDO_DISABLED'] = '1'
        report_unihash(os.getcwd(), ss['task'], d)
}
