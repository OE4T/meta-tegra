DESCRIPTION = "Install tegra bup payload file in the location expected by nv_update_engine"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

COMPATIBLE_MACHINE = "(tegra)"

UBOOT_SUFFIX ??= "bin"
UBOOT_IMAGE ?= "u-boot-${MACHINE}.${UBOOT_SUFFIX}"

def bupfile_basename(d):
    bootloader = d.getVar('PREFERRED_PROVIDER_virtual/bootloader')
    soc = d.getVar('SOC_FAMILY') or ''
    if bootloader is None:
        bootloader = 'cboot' if soc == 'tegra194' else 'u-boot'
    if bootloader.startswith('cboot'):
        if bb.utils.to_boolean(d.getVar('INITRAMFS_IMAGE_BUNDLE')):
            return "${KERNEL_IMAGETYPE}-initramfs-${MACHINE}"
        return "${INITRAMFS_IMAGE}-${MACHINE}"
    return "${UBOOT_IMAGE}"

def bup_dependency(d):
    bootloader = d.getVar('PREFERRED_PROVIDER_virtual/bootloader')
    soc = d.getVar('SOC_FAMILY') or ''
    if bootloader is None:
        bootloader = 'cboot' if soc == 'tegra194' else 'u-boot'
    if bootloader.startswith('cboot'):
        if bb.utils.to_boolean(d.getVar('INITRAMFS_IMAGE_BUNDLE')):
            return "kernel-bup-payload:do_deploy"
        return "${INITRAMFS_IMAGE}:do_image_complete"
    return "u-boot-bup-payload:do_deploy"

BUPFILE = "${@bupfile_basename(d)}.bup-payload"

do_install() {
    install -d ${D}/opt/ota_package/
    install -m 0644 ${DEPLOY_DIR_IMAGE}/${BUPFILE} ${D}/opt/ota_package/bl_update_payload
}

ALLOW_EMPTY_${PN} = "1"

do_install[depends] += "${@bup_dependency(d)}"
FILES_${PN} = "/opt/ota_package/bl_update_payload"
RDEPENDS_${PN} += "tegra-redundant-boot"
PACKAGE_ARCH = "${MACHINE_ARCH}"
