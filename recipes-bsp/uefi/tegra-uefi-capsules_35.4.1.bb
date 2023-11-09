DESCRIPTION = "Generate UEFI capsules for bup paylods"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit tegra-bup deploy

TEGRA_UEFI_CAPSULE_SIGNING_CLASS ??= "tegra-uefi-capsule-signing"
inherit ${TEGRA_UEFI_CAPSULE_SIGNING_CLASS}

TEGRA_UEFI_CAPSULE_SIGNING_EXTRA_DEPS ??= ""

COMPATIBLE_MACHINE = "(tegra)"

DEPENDS += "tegra-bup-payload"

GUID:tegra194 ?= "be3f5d68-7654-4ed2-838c-2a2faf901a78"
GUID:tegra234 ?= "bf0d4599-20d4-414e-b2c5-3595b1cda402"

do_compile() {
    sign_uefi_capsules
}

CAPSULE_DIR = "/opt/nvidia/UpdateCapsule"

do_install() {
    install -d ${D}${CAPSULE_DIR}
    if [ -e ${B}/tegra-bl.cap ]; then
        install -m 0644 ${B}/tegra-bl.cap ${D}${CAPSULE_DIR}
    fi
    if [ -e ${B}/tegra-kernel.cap ]; then
        install -m 0644 ${B}/tegra-kernel.cap ${D}${CAPSULE_DIR}
    fi
}

FILES:${PN} += "${CAPSULE_DIR}"
PACKAGE_ARCH = "${MACHINE_ARCH}"

do_deploy() {
    install -d ${DEPLOYDIR}
    if [ -e ${B}/tegra-bl.cap ]; then
	BL_NAME=${TNSPEC_MACHINE}-tegra-bl.cap
	install -m 0644 ${B}/tegra-bl.cap ${DEPLOYDIR}/$BL_NAME
	ln -s -r ${DEPLOYDIR}/$BL_NAME ${DEPLOYDIR}/tegra-bl.cap
    fi
    if [ -e ${B}/tegra-kernel.cap ]; then
	KERNEL_NAME=${TNSPEC_MACHINE}-tegra-kernel.cap
	install -m 0644 ${B}/tegra-kernel.cap ${DEPLOYDIR}/$KERNEL_NAME
	ln -s -r ${DEPLOYDIR}/$KERNEL_NAME ${DEPLOYDIR}/tegra-kernel.cap
    fi
}

addtask deploy after do_install

do_compile[depends] += "${@bup_dependency(d)} ${TEGRA_UEFI_CAPSULE_SIGNING_EXTRA_DEPS}"
