DESCRIPTION = "Minimal initramfs image for initrd flashing"
LICENSE = "MIT"

TEGRA_INITRD_FLASH_INSTALL ??= ""

TEGRA_INITRD_FLASH_BASEUTILS ?= "busybox"

PACKAGE_INSTALL = "\
    tegra-firmware-xusb \
    ${TEGRA_INITRD_FLASH_BASEUTILS} \
    ${ROOTFS_BOOTSTRAP_INSTALL} \
    ${TEGRA_INITRD_FLASH_INSTALL} \
    ${MACHINE_ESSENTIAL_EXTRA_RDEPENDS} \
    ${MACHINE_ESSENTIAL_EXTRA_RRECOMMENDS} \
    kernel-module-dummy \
    kernel-module-uas \
    kernel-module-tegra-bpmp-thermal \
    kernel-module-pwm-tegra \
    kernel-module-pwm-fan \
    nv-kernel-module-pcie-tegra264 \
    nv-kernel-module-ufs-tegra \
    tegra-target-flash-scripts \
"

IMAGE_FEATURES = ""
IMAGE_LINGUAS = ""

COPY_LIC_MANIFEST = "0"
COPY_LIC_DIRS = "0"

COMPATIBLE_MACHINE = "(tegra)"

IMAGE_ROOTFS_SIZE = "32768"
IMAGE_ROOTFS_EXTRA_SPACE = "0"
IMAGE_NAME_SUFFIX = ""

FORCE_RO_REMOVE ?= "1"

KERNEL_ARGS:remove = "console=tty0"

inherit core-image

IMAGE_FSTYPES = "${TEGRA_INITRD_FLASH_INITRAMFS_FSTYPES}"

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
        if ss['task'] in ['image_complete','image_qa']:
            os.environ['PSEUDO_DISABLED'] = '1'
        report_unihash(os.getcwd(), ss['task'], d)
}
