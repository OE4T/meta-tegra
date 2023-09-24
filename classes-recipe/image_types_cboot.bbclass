CBOOTIMG_KERNEL ?= "${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}"
TEGRA_UEFI_SIGNING_CLASS ??= "tegra-uefi-signing"

inherit ${TEGRA_UEFI_SIGNING_CLASS}

oe_cbootimg() {
    bbfatal "This image type only supported on tegra platforms"
}

sign_bootimg() {
    tegra_uefi_attach_sign "$1"
    rm "$1"
    mv "$1.signed" "$1"
}
oe_cbootimg_common() {
    outfile="$2"
    [ -n "$outfile" ] || outfile="${IMGDEPLOYDIR}/$1.cboot"
    ${STAGING_BINDIR_NATIVE}/tegra-flash/mkbootimg \
        --kernel ${CBOOTIMG_KERNEL} \
        --ramdisk ${IMGDEPLOYDIR}/$1 \
        --cmdline "${KERNEL_ARGS}" \
        --output "$outfile"
    sign_bootimg "$outfile"
    [ -n "$2" ] || ln -sf $1.cboot ${IMGDEPLOYDIR}/${IMAGE_LINK_NAME}.cboot
}
oe_cbootimg:tegra() {
    oe_cbootimg_common "$@"
}

make_cboot_image() {
    local type="$1"
    oe_cbootimg ${IMAGE_NAME}.${type}
}
make_cboot_image[vardepsexclude] += "DATETIME"

CONVERSIONTYPES =+ "cboot"
IMAGE_TYPES += "cpio.gz.cboot"

CONVERSION_DEPENDS_cboot = "tegra-flashtools-native virtual/kernel:do_deploy ${TEGRA_UEFI_SIGNING_TASKDEPS}"
CONVERSION_CMD:cboot = "make_cboot_image ${type}"
