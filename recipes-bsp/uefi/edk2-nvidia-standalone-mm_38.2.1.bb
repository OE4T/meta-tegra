require edk2-firmware-tegra-38.2.1.inc

DESCRIPTION = "Standalone Memory Manager for Tegra platforms"

COMPATIBLE_MACHINE = "(tegra)"

SRC_URI += "file://0006-sptool-update-the-python-script-for-standalone-mm-pa.patch;patchdir=../edk2-nvidia"

SMM_NAME = "StandaloneMmJetson"
SMM_NAME:tegra234 = "StandaloneMmOptee"

EDK2_PLATFORM = "${SMM_NAME}"
EDK2_PLATFORM_DSC = "Platform/NVIDIA/${SMM_NAME}/${SMM_NAME}.dsc"
EDK2_BIN_NAME = "standalone_mm_optee.bin"
EDK2_PLATFORM:tegra234 = "${SMM_NAME}"
EDK2_PLATFORM_DSC:tegra234 = "Platform/NVIDIA/${SMM_NAME}/${SMM_NAME}.dsc"
EDK2_BIN_NAME = "standalonemm_jetson.pkg"
EDK2_BIN_NAME:tegra234 = "standalone_mm_optee.bin"

TEGRA_BUILD_GUID = '-D "BUILD_GUID=3a7930b4-77f6-463e-b2c1-2436b0af492b" -D "BUILD_NAME=${EDK2_PLATFORM}"'

do_compile:append:tegra264() {
    rm -rf ${B}/images
    mkdir ${B}/images
    ${PYTHON} ${S_EDK2_NVIDIA}/Silicon/NVIDIA/edk2nv/sptool.py \
        --image ${B}/Build/${EDK2_PLATFORM}/${EDK2_BUILD_MODE}_${EDK_COMPILER}/FV/UEFI_MM.Fv \
        --manifest ${B}/Build/${EDK2_PLATFORM}/${EDK2_BUILD_MODE}_${EDK_COMPILER}/AARCH64/Silicon/NVIDIA/StandaloneMm/Manifest/ManifestStmmJetson/OUTPUT/StandaloneMmJetson.dtb \
        --output ${B}/images/${EDK2_BIN_NAME}
}

do_compile:append:tegra234() {
    rm -rf ${B}/images
    mkdir ${B}/images
    ${PYTHON} ${S_EDK2_NVIDIA}/Silicon/NVIDIA/edk2nv/FormatUefiBinary.py \
        ${B}/Build/${EDK2_PLATFORM}/${EDK2_BUILD_MODE}_${EDK_COMPILER}/FV/UEFI_MM.Fv \
        ${B}/images/${EDK2_BIN_NAME}
}

do_install() {
    install -d ${D}${datadir}/edk2-nvidia
    install -m 0644 ${B}/images/${EDK2_BIN_NAME} ${D}${datadir}/edk2-nvidia/
}

do_deploy() {
    :
}
do_deploy:tegra264() {
    install -D -m 0644 -t ${DEPLOYDIR} ${B}/images/${EDK2_BIN_NAME}
}

FILES:${PN} = "${datadir}/edk2-nvidia"
