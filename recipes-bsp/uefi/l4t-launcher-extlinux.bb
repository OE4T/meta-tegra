DESCRIPTION = "Generate an extlinux.conf for use with L4TLauncher UEFI application"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

COMPATIBLE_MACHINE = "(tegra)"

DEPENDS = "tegra-flashtools-native dtc-native"

TEGRA_UEFI_SIGNING_CLASS ??= "tegra-uefi-signing"

inherit l4t-extlinux-config kernel-artifact-names python3native ${TEGRA_UEFI_SIGNING_CLASS}

KERNEL_ARGS ??= ""
DTBFILE ?= "${@os.path.basename(d.getVar('KERNEL_DEVICETREE').split()[0])}"
EXTERNAL_KERNEL_DEVICETREE ?= "${@'${RECIPE_SYSROOT}/boot/devicetree' if d.getVar('PREFERRED_PROVIDER_virtual/dtb') else ''}"

# Need to handle:
#  a) Kernel with no initrd/initramfs
#  b) Kernel with bundled initramfs
#  c) Kernel with separate initrd
def compute_dependencies(d):
    deps = "virtual/kernel:do_deploy"
    initramfs_image = d.getVar('INITRAMFS_IMAGE') or ''
    if initramfs_image != '' and (d.getVar('INITRAMFS_IMAGE_BUNDLE') or '') != '1':
        deps += " %s:do_image_complete" % initramfs_image
    return deps


PATH =. "${STAGING_BINDIR_NATIVE}/tegra-flash:"

do_configure() {
    :
}

do_compile() {
    if [ -n "${INITRAMFS_IMAGE}" ]; then
        if [ "${INITRAMFS_IMAGE_BUNDLE}" = "1" ]; then
            cp -L ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}-${INITRAMFS_LINK_NAME}.bin ${B}/${KERNEL_IMAGETYPE}
        else
            cp -L ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}-${MACHINE}.bin ${B}/${KERNEL_IMAGETYPE}
            cp -L ${DEPLOY_DIR_IMAGE}/${INITRAMFS_IMAGE}-${MACHINE}.cpio.gz ${B}/initrd
        fi
    else
        cp -L ${DEPLOY_DIR_IMAGE}/${KERNEL_IMAGETYPE}-${MACHINE}.bin ${B}/${KERNEL_IMAGETYPE}
    fi
}
do_compile[depends] += "${@compute_dependencies(d)}"
do_compile[dirs] = "${B}"

python do_concat_dtb_overlays() {
   if d.getVar('UBOOT_EXTLINUX_FDT'):
        oe4t.dtbutils.concat_dtb_overlays(d.getVar('DTBFILE'),
                                          d.getVar('TEGRA_PLUGIN_MANAGER_OVERLAYS'),
                                          os.path.join(d.getVar('B'), d.getVar('DTBFILE')), d)
}
do_concat_dtb_overlays[dirs] = "${B}"
do_concat_dtb_overlays[depends] += "virtual/kernel:do_deploy"
do_concat_dtb_overlays[depends] += "${@'virtual/dtb:do_populate_sysroot' if d.getVar('PREFERRED_PROVIDER_virtual/dtb') else ''}"

addtask concat_dtb_overlays after do_configure before do_sign_files

sign_extlinux_files() {
    while [ $# -gt 0 ]; do
        tegra_uefi_split_sign "$1"
        shift
    done
}

do_sign_files() {
    local files_to_sign="extlinux.conf"
    if [ -n "${UBOOT_EXTLINUX_FDT}" ]; then
        files_to_sign="$files_to_sign ${DTBFILE}"
    fi
    if [ -n "${INITRAMFS_IMAGE}" -a "${INITRAMFS_IMAGE_BUNDLE}" != "1" ]; then
        files_to_sign="$files_to_sign initrd"
    fi
    sign_extlinux_files $files_to_sign
}
do_sign_files[dirs] = "${B}"
do_sign_files[depends] += "${TEGRA_UEFI_SIGNING_TASKDEPS}"

addtask sign_files after do_compile do_create_extlinux_config do_concat_dtb_overlays before do_install

do_install() {
    install -d ${D}/boot/extlinux
    install -m 0644 ${B}/${KERNEL_IMAGETYPE} ${D}/boot/
    if [ -n "${UBOOT_EXTLINUX_FDT}" ]; then
        install -m 0644 ${B}/${DTBFILE}* ${D}/boot/
    fi
    if [ -n "${INITRAMFS_IMAGE}" -a "${INITRAMFS_IMAGE_BUNDLE}" != "1" ]; then
        install -m 0644 ${B}/initrd* ${D}/boot/
    fi
    install -m 0644 ${B}/extlinux.conf* ${D}/boot/extlinux/
}

FILES:${PN} = "/boot"
PACKAGE_ARCH = "${MACHINE_ARCH}"
