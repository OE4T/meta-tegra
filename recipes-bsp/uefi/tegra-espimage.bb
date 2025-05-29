DESCRIPTION = "EFI system partition image for Jetson platforms"
LICENSE = "MIT"

IMAGE_FEATURES = ""
IMAGE_LINGUAS = ""
COPY_LIC_MANIFEST = "0"
COPY_LIC_DIRS = "0"
COMPATIBLE_MACHINE = "(tegra)"
IMAGE_ROOTFS_SIZE = "65536"
IMAGE_ROOTFS_MAXSIZE = "65536"
IMAGE_ROOTFS_EXTRA_SPACE = "0"
FORCE_RO_REMOVE = "1"
IMAGE_NAME_SUFFIX = ""

EFI_PROVIDER ??= "l4t-launcher"

PACKAGE_INSTALL = "${EFI_PROVIDER}"

inherit core-image

IMAGE_FSTYPES:forcevariable = "esp"

SSTATE_SKIP_CREATION:task-image-complete = "0"
SSTATE_SKIP_CREATION:task-image-qa = "0"
do_image_complete[vardepsexclude] += "rm_work_rootfs"
IMAGE_PREPROCESS_COMMAND = "remove_unused_stuff;"
inherit nopackages

remove_unused_stuff() {
    for f in ${IMAGE_ROOTFS}/*; do
        local fbase=$(basename $f | tr '[:lower:]' '[:upper:]')
        [ "$fbase" != "EFI" ] || continue
        rm -rf $f
    done
}

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
