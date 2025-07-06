require edk2-firmware-tegra-36.4.4.inc

DESCRIPTION = "UEFI EDK2 Firmware for Jetson platforms"

PROVIDES = "virtual/bootloader"

DEPENDS += "dtc-native"

TEGRA_UEFI_SIGNING_CLASS ??= "tegra-uefi-signing"

inherit l4t_bsp deploy ${TEGRA_UEFI_SIGNING_CLASS}

EDK2_PLATFORM = "${@'JetsonMinimal' if bb.utils.to_boolean(d.getVar('TEGRA_UEFI_MINIMAL')) else 'Jetson'}"
EDK2_PLATFORM_DSC = "Platform/NVIDIA/NVIDIA.common.dsc"
EDK2_BIN_NAME = "uefi_jetson.bin"

SRC_URI += "file://nvbuildconfig.py"

do_configure:append() {
    ${PYTHON} ${WORKDIR}/nvbuildconfig.py ${S_EDK2_NVIDIA}/Platform/NVIDIA/Kconfig ${S_EDK2_NVIDIA}/Platform/NVIDIA/${EDK2_PLATFORM}/Jetson.defconfig ${B}/nvidia-config/Jetson/.config ${B}/nvidia-config/Jetson/config.dsc.inc
}

def fmp_lowest_version(d):
    verparts = d.getVar('L4T_VERSION').split('.')
    branch = int(verparts[0])
    branch_high = (branch >> 8) & 0xff
    branch_low = branch & 0xff
    major = int(verparts[1]) & 0xff
    minor = int(verparts[2]) & 0xff
    return "0x%02x%02x%02x%02x" % (branch_high, branch_low, major, minor)

do_compile:append() {
    rm -rf ${B}/images
    mkdir ${B}/images
    ${PYTHON} ${S_EDK2_NVIDIA}/Silicon/NVIDIA/edk2nv/FormatUefiBinary.py \
        ${B}/Build/${EDK2_PLATFORM}/${EDK2_BUILD_MODE}_${EDK_COMPILER}/FV/UEFI_NS.Fv \
        ${B}/images/${EDK2_BIN_NAME}.tmp
    mv ${B}/images/${EDK2_BIN_NAME}.tmp ${B}/images/${EDK2_BIN_NAME}
    if ${@'false' if bb.utils.to_boolean(d.getVar('TEGRA_UEFI_MINIMAL')) else 'true'}; then
        ${PYTHON} ${S_EDK2_NVIDIA}/Silicon/NVIDIA/edk2nv/FormatUefiBinary.py \
                  ${B}/Build/${EDK2_PLATFORM}/${EDK2_BUILD_MODE}_${EDK_COMPILER}/AARCH64/L4TLauncher.efi \
                  ${B}/images/BOOTAA64.efi
    fi
    for f in ${B}/Build/${EDK2_PLATFORM}/${EDK2_BUILD_MODE}_${EDK_COMPILER}/AARCH64/Silicon/NVIDIA/Tegra/DeviceTree/DeviceTree/OUTPUT/*.dtb; do
        [ -e "$f" ] || continue
        fbase=$(basename "$f" ".dtb")
        cp $f ${B}/images/$fbase.dtbo
    done
    fdtput -t i ${B}/images/L4TConfiguration.dtbo "/fragment@0/__overlay__/firmware/uefi" fmp-lowest-supported-version ${@fmp_lowest_version(d)}
    cp ${B}/images/L4TConfiguration.dtbo ${B}/images/L4TConfiguration-rcmboot.dtbo
    fdtput -t s ${B}/images/L4TConfiguration-rcmboot.dtbo "/fragment@0/__overlay__/firmware/uefi/variables/gNVIDIATokenSpaceGuid/DefaultBootPriority" data boot.img
}

sign_efi_app() {
    tegra_uefi_sbsign "$1"
}

do_sign_efi_launcher() {
    sign_efi_app images/BOOTAA64.efi
}
do_sign_efi_launcher[dirs] = "${B}"
do_sign_efi_launcher[depends] += "${TEGRA_UEFI_SIGNING_TASKDEPS}"

addtask sign_efi_launcher after do_compile before do_install

do_install() {
    install -d ${D}${EFIDIR}
    if ${@'false' if bb.utils.to_boolean(d.getVar('TEGRA_UEFI_MINIMAL')) else 'true'}; then
        install -m 0644 ${B}/images/BOOTAA64.efi ${D}${EFIDIR}/${EFI_BOOT_IMAGE}
    fi
}

PACKAGES = "l4t-launcher"
FILES:l4t-launcher = "${EFIDIR}"
INSANE_SKIP:l4t-launcher = "buildpaths"

do_deploy() {
    install -d ${DEPLOYDIR}
    install -m 0644 ${B}/images/${EDK2_BIN_NAME} ${DEPLOYDIR}/
    for dtb in ${TEGRA_BOOTCONTROL_OVERLAYS} L4TConfiguration-rcmboot.dtbo; do
	[ -e ${B}/images/$dtb ] || continue
	install -m 0644 ${B}/images/$dtb ${DEPLOYDIR}/
    done
}
do_deploy[depends] += "${@'l4t-launcher-rootfs-ab-config:do_deploy' if bb.utils.to_boolean(d.getVar('USE_REDUNDANT_FLASH_LAYOUT')) else ''}"

addtask deploy before do_build after do_install
