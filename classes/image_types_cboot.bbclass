CBOOTIMG_KERNEL ?= "${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}"

inherit tegra-uefi-signing

oe_cbootimg() {
    bbfatal "This image type only supported on tegra platforms"
}

# Override this function in a bbappend to
# implement other signing mechanisms
sign_bootimg() {
    if [ -n "${TEGRA_UEFI_DB_KEY}" -a -n "${TEGRA_UEFI_DB_CERT}" ]; then
        tegra_uefi_attach_sign "$1"
	rm "$1"
	mv "$1.signed" "$1"
    fi
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
    oe_cbootimg ${IMAGE_NAME}${IMAGE_NAME_SUFFIX}.${type}
}
make_cboot_image[vardepsexclude] += "DATETIME"

CONVERSIONTYPES =+ "cboot"
IMAGE_TYPES += "cpio.gz.cboot"

CONVERSION_DEPENDS_cboot = "tegra-flashtools-native virtual/kernel:do_deploy ${TEGRA_UEFI_SIGNING_TASKDEPS}"
CONVERSION_CMD:cboot = "make_cboot_image ${type}"
