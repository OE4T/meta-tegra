CBOOTIMG_KERNEL ?= "${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}"

CBOOTTOOLSDIRPFX = "tegra186"
CBOOTTOOLSDIRPFX:tegra210 = "tegra210"

oe_cbootimg() {
    outfile="$2"
    [ -n "$outfile" ] || outfile="${IMGDEPLOYDIR}/$1.cboot"
    ${STAGING_BINDIR_NATIVE}/${CBOOTTOOLSDIRPFX}-flash/mkbootimg \
        --kernel ${CBOOTIMG_KERNEL} \
        --ramdisk ${IMGDEPLOYDIR}/$1 \
        --cmdline "${KERNEL_ARGS}" \
        --output "$outfile"
    [ -n "$2" ] || ln -sf $1.cboot ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.cboot
}

make_cboot_image() {
    local type="$1"
    oe_cbootimg ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}
}
make_cboot_image[vardepsexclude] += "DATETIME"

CONVERSIONTYPES =+ "cboot"
IMAGE_TYPES += "cpio.gz.cboot"

CONVERSION_DEPENDS_cboot = "${CBOOTTOOLSDIRPFX}-flashtools-native virtual/kernel:do_deploy"
CONVERSION_CMD:cboot = "make_cboot_image ${type}"
