DESCRIPTION = "Install tegra bup payload file in the location expected by nv_update_engine"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

COMPATIBLE_MACHINE = "(tegra)"

UBOOT_SUFFIX ??= "bin"
UBOOT_IMAGE ?= "u-boot-${MACHINE}.${UBOOT_SUFFIX}"

BUPFILE = "${@'${KERNEL_IMAGETYPE}-initramfs-${MACHINE}.bup-payload' if d.getVar("PREFERRED_PROVIDER_virtual/bootloader").startswith("cboot") else '${UBOOT_IMAGE}.bup-payload'}"

do_install() {
    if [ -z "${DEPLOY_DIR_IMAGE}/${BUPFILE}" ]; then
        install -d ${D}/opt/ota_package/
        install -m 0644 ${DEPLOY_DIR_IMAGE}/${BUPFILE} ${D}/opt/ota_package/bl_update_payload
    fi
}

ALLOW_EMPTY_${PN} = "1"

do_install[depends] += "${@'kernel-bup-payload:do_deploy' if d.getVar('PREFERRED_PROVIDER_virtual/bootloader').startswith('cboot') else 'u-boot-bup-payload:do_deploy'}"
FILES_${PN} = "/opt/ota_package/bl_update_payload"
RDEPENDS_${PN} += "tegra-redundant-boot"
PACKAGE_ARCH = "${MACHINE_ARCH}"
