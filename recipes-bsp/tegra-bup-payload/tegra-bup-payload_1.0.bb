DESCRIPTION = "Install tegra bup payload file in the location expected by nv_update_engine"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

COMPATIBLE_MACHINE = "(tegra)"

inherit tegra-bup

do_install() {
    install -d ${D}/opt/ota_package/
    if [ -e ${DEPLOY_DIR_IMAGE}/${BUPFILENAME}.bl_only.bup-payload ]; then
        install -m 0644 ${DEPLOY_DIR_IMAGE}/${BUPFILENAME}.bl_only.bup-payload ${D}/opt/ota_package/bl_only_payload
    fi
    if [ -e ${DEPLOY_DIR_IMAGE}/${BUPFILENAME}.kernel_only.bup-payload ]; then
        install -m 0644 ${DEPLOY_DIR_IMAGE}/${BUPFILENAME}.kernel_only.bup-payload ${D}/opt/ota_package/kernel_only_payload
    fi
}

ALLOW_EMPTY:${PN} = "1"

do_install[depends] += "${@bup_dependency(d)}"
FILES:${PN} = "/opt/ota_package"
RDEPENDS:${PN} += "tegra-redundant-boot-update-engine"
# For UEFI build - remove once buildpaths issue is resolved there
INSANE_SKIP:${PN} = "buildpaths"
PACKAGE_ARCH = "${MACHINE_ARCH}"
