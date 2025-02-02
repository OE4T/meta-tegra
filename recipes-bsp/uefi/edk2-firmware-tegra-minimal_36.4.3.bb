require edk2-firmware-tegra-36.4.3.inc

DESCRIPTION = "UEFI EDK2 Minimal Firmware for Jetson platforms"

TEGRA_UEFI_SIGNING_CLASS ??= "tegra-uefi-signing"

inherit l4t_bsp deploy ${TEGRA_UEFI_SIGNING_CLASS}

EDK2_PLATFORM = "JetsonMinimal"
TEGRA_UEFI_MINIMAL = "1"
EDK2_PLATFORM_DSC = "Platform/NVIDIA/NVIDIA.common.dsc"
EDK2_BIN_NAME = "uefi_jetson_minimal.bin"

SRC_URI += "file://nvbuildconfig.py"

do_configure:append() {
    ${PYTHON} ${UNPACKDIR}/nvbuildconfig.py ${S_EDK2_NVIDIA}/Platform/NVIDIA/Kconfig ${S_EDK2_NVIDIA}/Platform/NVIDIA/${EDK2_PLATFORM}/Jetson.defconfig ${B}/nvidia-config/Jetson/.config ${B}/nvidia-config/Jetson/config.dsc.inc
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
    install -m 0644 ${B}/images/${EDK2_BIN_NAME} ${DEPLOYDIR}/
}
# Downstream consumers will need the dtb overlays created by the
# normal build
do_deploy[depends] += "virtual/bootloader:do_deploy"

addtask deploy before do_build after do_install
