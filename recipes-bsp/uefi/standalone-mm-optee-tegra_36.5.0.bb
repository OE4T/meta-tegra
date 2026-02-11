require edk2-firmware-tegra-36.5.0.inc

DESCRIPTION = "Standalone Memory Manager for OP-TEE on Jetson platforms"

EDK2_PLATFORM = "StandaloneMmOptee"
EDK2_PLATFORM_DSC = "Platform/NVIDIA/StandaloneMmOptee/StandaloneMmOptee.dsc"
EDK2_BIN_NAME = "standalone_mm_optee.bin"

TEGRA_BUILD_GUID = '-D "BUILD_GUID=fb0e2152-1441-49e0-b376-5f8593d66678" -D "BUILD_NAME=$[EDK2_PLATFORM}"'

do_compile:append() {
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

FILES:${PN} = "${datadir}/edk2-nvidia"
