CBOOTIMG_KERNEL ?= "${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}"

oe_cbootimg() {
    bbfatal "This image type only supported on tegra186/tegra194 platforms"
}

oe_cbootimg_common() {
    outfile="$2"
    [ -n "$outfile" ] || outfile="${IMGDEPLOYDIR}/$1.cboot"
    ${STAGING_BINDIR_NATIVE}/tegra186-flash/mkbootimg \
        --kernel ${CBOOTIMG_KERNEL} \
        --ramdisk ${IMGDEPLOYDIR}/$1 \
        --output "$outfile"
    [ -n "$2" ] || ln -sf $1.cboot ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.cboot
}
oe_cbootimg_tegra186() {
    oe_cbootimg_common "$@"
}
oe_cbootimg_tegra194() {
    oe_cbootimg_common "$@"
}

make_cboot_image() {
    local type="$1"
    oe_cbootimg ${IMAGE_NAME}.rootfs.${type}
}
make_cboot_image[vardepsexclude] += "DATETIME"

CONVERSIONTYPES =+ "cboot"
IMAGE_TYPES += "cpio.gz.cboot"

CONVERSION_DEPENDS_cboot = "tegra186-flashtools-native virtual/kernel:do_deploy"
CONVERSION_CMD_cboot = "make_cboot_image ${type}"
