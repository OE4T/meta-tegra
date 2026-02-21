require edk2-firmware-tegra-35.6.4.inc

DESCRIPTION = "Standalone Memory Manager for OP-TEE on Jetson platforms"

EDK2_PLATFORM = "StandaloneMmOptee"
EDK2_PLATFORM_DSC = "Platform/NVIDIA/StandaloneMmOptee/StandaloneMmOptee.dsc"
EDK2_BIN_NAME = "standalone_mm_optee.bin"

do_compile:append() {
    rm -rf ${B}/images
    mkdir ${B}/images
    python3 ${S_EDK2_NVIDIA}/Silicon/NVIDIA/Tools/FormatUefiBinary.py \
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
