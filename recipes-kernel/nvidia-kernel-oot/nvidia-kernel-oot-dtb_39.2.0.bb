DESCRIPTION = "Virtual/dtb provider for Jetson Linux device trees"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

INHIBIT_DEFAULT_DEPS = "1"
DEPENDS += "nvidia-kernel-oot"

inherit deploy kernel-arch

PROVIDES = "virtual/dtb"

PACKAGE_ARCH = "${MACHINE_ARCH}"

do_deploy() {
    for dtb in ${KERNEL_DEVICETREE}; do
        dtbf="${STAGING_DIR_HOST}/boot/devicetree/$dtb"
        if [ ! -f "$dtbf" ]; then
            bbfatal "Not found: $dtbf"
        fi
    done
    install -d ${DEPLOYDIR}/devicetree
    install -m 0644 ${STAGING_DIR_HOST}/boot/devicetree/* ${DEPLOYDIR}/devicetree/
}

addtask deploy before do_build after do_install

ALLOW_EMPTY:${PN} = "1"
