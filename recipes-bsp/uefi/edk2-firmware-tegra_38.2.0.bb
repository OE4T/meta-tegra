require edk2-firmware-tegra-38.2.0.inc

DESCRIPTION = "UEFI EDK2 Firmware for Jetson platforms"

PROVIDES = "virtual/bootloader"

DEPENDS += "dtc-native"

TEGRA_UEFI_SIGNING_CLASS ??= "tegra-uefi-signing"

inherit l4t_bsp deploy ${TEGRA_UEFI_SIGNING_CLASS}

EDK2_PLATFORM_DSC = "Platform/NVIDIA/NVIDIA.common.dsc"
TEGRA_EDK2_PLATFORM = "UNKNOWN"
TEGRA_EDK2_PLATFORM:tegra234 = "t23x"
TEGRA_EDK2_PLATFORM:tegra264 = "t26x"
TEGRA_EDK2_CONFIGURATION ?= "general"
EDK2_PLATFORM = "${TEGRA_EDK2_PLATFORM}_${TEGRA_EDK2_CONFIGURATION}"
EDK2_BIN_NAME = "uefi_${EDK2_PLATFORM}.bin"

SRC_URI += "file://nvbuildconfig.py"

do_configure:append() {
    mkdir -p ${B}/nvidia-config/Tegra/${EDK2_PLATFORM}
    ${PYTHON} ${UNPACKDIR}/nvbuildconfig.py ${S_EDK2_NVIDIA}/Platform/NVIDIA/Kconfig ${S_EDK2_NVIDIA}/Platform/NVIDIA/Tegra/DefConfigs/${EDK2_PLATFORM}.defconfig ${B}/nvidia-config/Tegra/${EDK2_PLATFORM}
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

    PATH="${WORKSPACE}:${BTOOLS_PATH}:$PATH" \
    build \
       --arch "${EDK2_ARCH}" \
       --buildtarget ${EDK2_BUILD_MODE} \
       --tagname ${EDK_COMPILER} \
       --platform ${S_EDK2_NVIDIA}/Platform/NVIDIA/L4TLauncher/L4TLauncher.dsc \
       ${@oe.utils.parallel_make_argument(d, "-n %d")} \
       ${EDK2_EXTRA_BUILD} -D "BUILD_GUID=be4936a8-d418-405c-9f5c-a61723884a40" -D "BUILD_NAME=L4TLauncher"

    PATH="${WORKSPACE}:${BTOOLS_PATH}:$PATH" \
    build \
       --arch "${EDK2_ARCH}" \
       --buildtarget ${EDK2_BUILD_MODE} \
       --tagname ${EDK_COMPILER} \
       --platform ${S_EDK2_NVIDIA}/Platform/NVIDIA/DeviceTree/DeviceTree.dsc \
       ${@oe.utils.parallel_make_argument(d, "-n %d")} \
       ${EDK2_EXTRA_BUILD} -D "BUILD_GUID=4a17d121-7753-4341-b4e4-009550283be0" -D "BUILD_NAME=DeviceTree"

    rm -rf ${B}/images
    mkdir ${B}/images

    ${PYTHON} ${S_EDK2_NVIDIA}/Silicon/NVIDIA/edk2nv/FormatUefiBinary.py \
        ${B}/Build/${EDK2_PLATFORM}/${EDK2_BUILD_MODE}_${EDK_COMPILER}/FV/UEFI_NS.Fv \
        ${B}/images/${EDK2_BIN_NAME}.tmp
    mv ${B}/images/${EDK2_BIN_NAME}.tmp ${B}/images/${EDK2_BIN_NAME}

    cp ${B}/Build/L4TLauncher/${EDK2_BUILD_MODE}_${EDK_COMPILER}/AARCH64/L4TLauncher.efi ${B}/images/BOOTAA64.efi

    for f in ${B}/Build/DeviceTree/${EDK2_BUILD_MODE}_${EDK_COMPILER}/AARCH64/Silicon/NVIDIA/Tegra/DeviceTree/DeviceTree/OUTPUT/*.dtb; do
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
do_sign_efi_launcher[file-checksums] += "${TEGRA_UEFI_SIGNING_FILECHECKSUMS}"

addtask sign_efi_launcher after do_compile before do_install

do_install() {
    install -d ${D}${EFIDIR}
    install -m 0644 ${B}/images/BOOTAA64.efi ${D}${EFIDIR}/${EFI_BOOT_IMAGE}
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
