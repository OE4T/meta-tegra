require edk2-firmware-tegra-36.5.0.inc

DESCRIPTION = "UEFI EDK2 Minimal Firmware for flashing Jetson platforms"

TEGRA_UEFI_SIGNING_CLASS ??= "tegra-uefi-signing"

inherit l4t_bsp deploy ${TEGRA_UEFI_SIGNING_CLASS}

EDK2_PLATFORM_DSC = "Platform/NVIDIA/NVIDIA.common.dsc"
TEGRA_EDK2_PLATFORM ??= "UNKNOWN"
TEGRA_EDK2_CONFIGURATION = "embedded"
EDK2_PLATFORM = "${TEGRA_EDK2_PLATFORM}_${TEGRA_EDK2_CONFIGURATION}"
TEGRA_FLASHVAR_UEFI_IMAGE ??= "uefi_${EDK2_PLATFORM}"
EDK2_BIN_NAME = "uefi_${EDK2_PLATFORM}.bin"

SRC_URI += "file://nvbuildconfig.py"

do_configure:append() {
    ${PYTHON} ${UNPACKDIR}/nvbuildconfig.py --kconfig-path=${S_EDK2_NVIDIA}/Platform/NVIDIA/Kconfig --output-dir=${B}/nvidia-config/Tegra/${EDK2_PLATFORM} ${S_EDK2_NVIDIA}/Platform/NVIDIA/Tegra/DefConfigs/${EDK2_PLATFORM}.defconfig ${@config_fragments(d)}
}

do_compile:append() {
    rm -rf ${B}/images
    mkdir ${B}/images
    ${PYTHON} ${S_EDK2_NVIDIA}/Silicon/NVIDIA/edk2nv/FormatUefiBinary.py \
        ${B}/Build/${EDK2_PLATFORM}/${EDK2_BUILD_MODE}_${EDK_COMPILER}/FV/UEFI_NS.Fv \
        ${B}/images/${EDK2_BIN_NAME}.tmp
    mv ${B}/images/${EDK2_BIN_NAME}.tmp ${B}/images/${EDK2_BIN_NAME}
}

do_install() {
    :
}

do_deploy() {
    install -d ${DEPLOYDIR}
    install -m 0644 ${B}/images/${EDK2_BIN_NAME} ${DEPLOYDIR}/${TEGRA_FLASHVAR_RCM_UEFI_IMAGE}.bin
}
# Downstream consumers will need the dtb overlays created by the
# normal build
do_deploy[depends] += "virtual/bootloader:do_deploy"

addtask deploy before do_build after do_install
