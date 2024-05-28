inherit l4t_bsp

DEPENDS += "edk2-basetools-tegra-native"

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
    if [ -e ${B}/${BUPFILENAME}.bl_only.bup-payload ]; then
        python3 ${PYTHON_BASETOOLS}/Capsule/GenerateCapsule.py \
            -v --encode --monotonic-count 1 \
            --fw-version "${BSP_VERSION32}" \
            --lsv "${BSP_VERSION32}" \
            --guid "${GUID}" \
            --signer-private-cert "${UEFI_CAPSULE_SIGNER_PRIVATE_CERT}" \
            --other-public-cert "${UEFI_CAPSULE_OTHER_PUBLIC_CERT}" \
            --trusted-public-cert "${UEFI_CAPSULE_TRUSTED_PUBLIC_CERT}" \
            -o ./tegra-bl.cap \
            ${B}/${BUPFILENAME}.bl_only.bup-payload
    fi
    if [ -e ${B}/${BUPFILENAME}.kernel_only.bup-payload ]; then
        python3 ${PYTHON_BASETOOLS}/Capsule/GenerateCapsule.py \
            -v --encode --monotonic-count 1 \
            --fw-version "${BSP_VERSION32}" \
            --lsv "${BSP_VERSION32}" \
            --guid "${GUID}" \
            --signer-private-cert "${UEFI_CAPSULE_SIGNER_PRIVATE_CERT}" \
            --other-public-cert "${UEFI_CAPSULE_OTHER_PUBLIC_CERT}" \
            --trusted-public-cert "${UEFI_CAPSULE_TRUSTED_PUBLIC_CERT}" \
            -o ./tegra-kernel.cap \
            ${B}/${BUPFILENAME}.kernel_only.bup-payload
    fi
}
