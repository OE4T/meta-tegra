DESCRIPTION = "Generate UEFI capsules for bup paylods"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

inherit l4t_bsp tegra-bup

COMPATIBLE_MACHINE = "(tegra)"

DEPENDS += "edk2-basetools-tegra-native tegra-bup-payload"

GUID:tegra194 ?= "be3f5d68-7654-4ed2-838c-2a2faf901a78"
GUID:tegra234 ?= "bf0d4599-20d4-414e-b2c5-3595b1cda402"

def get_hex_bsp_version(bsp_version):
    verparts = bsp_version.split('.')
    return hex(int(verparts[0])<<16 | int(verparts[1])<<8 | int(verparts[2]))

BSP_VERSION32 = "${@get_hex_bsp_version(d.getVar('L4T_VERSION'))}"

PYTHON_BASETOOLS = "${RECIPE_SYSROOT_NATIVE}/usr/bin/edk2-BaseTools/Source/Python"

UEFI_CAPSULE_SIGNER_PRIVATE_CERT ?= "${PYTHON_BASETOOLS}/Pkcs7Sign/TestCert.pem"
UEFI_CAPSULE_OTHER_PUBLIC_CERT ?= "${PYTHON_BASETOOLS}/Pkcs7Sign/TestSub.pub.pem"
UEFI_CAPSULE_TRUSTED_PUBLIC_CERT ?= "${PYTHON_BASETOOLS}/Pkcs7Sign/TestRoot.pub.pem"

# Override this function to have a secure signing server
# perform the capsule signing.
sign_uefi_capsules() {
    export PYTHONPATH="${PYTHONPATH}:${PYTHON_BASETOOLS}"
    if [ -e ${DEPLOY_DIR_IMAGE}/${BUPFILENAME}.bl_only.bup-payload ]; then
        python3 ${PYTHON_BASETOOLS}/Capsule/GenerateCapsule.py \
            -v --encode --monotonic-count 1 \
            --fw-version "${BSP_VERSION32}" \
            --lsv "${BSP_VERSION32}" \
            --guid "${GUID}" \
            --signer-private-cert "${UEFI_CAPSULE_SIGNER_PRIVATE_CERT}" \
            --other-public-cert "${UEFI_CAPSULE_OTHER_PUBLIC_CERT}" \
            --trusted-public-cert "${UEFI_CAPSULE_TRUSTED_PUBLIC_CERT}" \
            -o ./tegra-bl.cap \
            ${DEPLOY_DIR_IMAGE}/${BUPFILENAME}.bl_only.bup-payload
    fi
    if [ -e ${DEPLOY_DIR_IMAGE}/${BUPFILENAME}.kernel_only.bup-payload ]; then
        python3 ${PYTHON_BASETOOLS}/Capsule/GenerateCapsule.py \
            -v --encode --monotonic-count 1 \
            --fw-version "${BSP_VERSION32}" \
            --lsv "${BSP_VERSION32}" \
            --guid "${GUID}" \
            --signer-private-cert "${UEFI_CAPSULE_SIGNER_PRIVATE_CERT}" \
            --other-public-cert "${UEFI_CAPSULE_OTHER_PUBLIC_CERT}" \
            --trusted-public-cert "${UEFI_CAPSULE_TRUSTED_PUBLIC_CERT}" \
            -o ./tegra-kernel.cap \
            ${DEPLOY_DIR_IMAGE}/${BUPFILENAME}.kernel_only.bup-payload
    fi
}

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
INSANE_SKIP:${PN} += "buildpaths"
