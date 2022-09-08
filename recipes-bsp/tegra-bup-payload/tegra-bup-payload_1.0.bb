DESCRIPTION = "Install tegra bup payload file in the location expected by nv_update_engine"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

COMPATIBLE_MACHINE = "(tegra)"

inherit kernel-artifact-names

def bupfile_basename(d):
    if bb.utils.to_boolean(d.getVar('INITRAMFS_IMAGE_BUNDLE')):
        return "${KERNEL_IMAGETYPE}-${INITRAMFS_LINK_NAME}"
    return "${INITRAMFS_IMAGE}-${MACHINE}"

def bup_dependency(d):
    if bb.utils.to_boolean(d.getVar('INITRAMFS_IMAGE_BUNDLE')):
        return "kernel-bup-payload:do_deploy"
    return "${INITRAMFS_IMAGE}:do_image_complete"

BUPFILE = "${@bupfile_basename(d)}.bup-payload"

do_install() {
    install -d ${D}/opt/ota_package/
    install -m 0644 ${DEPLOY_DIR_IMAGE}/${BUPFILE} ${D}/opt/ota_package/bl_update_payload
}

ALLOW_EMPTY:${PN} = "1"

do_install[depends] += "${@bup_dependency(d)}"
FILES:${PN} = "/opt/ota_package/bl_update_payload"
RDEPENDS:${PN} += "tegra-redundant-boot-update-engine"
# For UEFI build - remove once buildpaths issue is resolved there
INSANE_SKIP:${PN} = "buildpaths"
PACKAGE_ARCH = "${MACHINE_ARCH}"
