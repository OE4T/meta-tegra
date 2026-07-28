require edk2-firmware-tegra-39.2.1.inc

DESCRIPTION = "UEFI EDK2 Firmware for Jetson platforms"

PROVIDES = "virtual/bootloader"

DEPENDS += "dtc-native"

TEGRA_UEFI_SIGNING_CLASS ??= "tegra-uefi-signing"

inherit l4t_bsp deploy ${TEGRA_UEFI_SIGNING_CLASS}

EDK2_PLATFORM_DSC = "Platform/NVIDIA/NVIDIA.common.dsc"
TEGRA_EDK2_PLATFORM ??= "UNKNOWN"
TEGRA_EDK2_CONFIGURATION ??= "general"
EDK2_PLATFORM = "${TEGRA_EDK2_PLATFORM}_${TEGRA_EDK2_CONFIGURATION}"
TEGRA_FLASHVAR_UEFI_IMAGE ??= "uefi_${EDK2_PLATFORM}"
EDK2_BIN_NAME = "uefi_${EDK2_PLATFORM}.bin"
TEGRA_UEFI_SYSTEM_IMAGE_TYPE_GUID ??= ""

SRC_URI += "file://nvbuildconfig.py"

TEGRA_MINIMAL_BOOT ??= "0"
TEGRA_MINIMAL_BOOT_HEADLESS ??= "1"

MINIMAL_BOOT_PATCHES = "file://0001-feat-capsule-updates-with-single-boot-configuration.patch;patchdir=../edk2-nvidia"

def minimal_configuration(d):
    result = []
    if bb.utils.to_boolean(d.getVar('TEGRA_MINIMAL_BOOT')):
        result += d.getVar('MINIMAL_BOOT_PATCHES').split()
        result.append("file://disable-unused-features.cfg")
        if not bb.utils.to_boolean(d.getVar('TEGRAFLASH_NO_INTERNAL_STORAGE') or '0') and not d.getVar('TNSPEC_BOOTDEV').startswith('nvme'):
            result.append("file://enable-{}.cfg".format('sdcard' if d.getVar('TNSPEC_BOOTDEV') == 'mmcblk1p1' else 'emmc'))
        if bb.utils.to_boolean(d.getVar('TEGRA_MINIMAL_BOOT_HEADLESS')):
            result.append("file://disable-display.cfg")
    return ' '.join(result)

SRC_URI += "${@minimal_configuration(d)}"

# The Kconfig/Kbuild files NVIDIA provides don't get the logic for
# setting the default boot timeout quite right, so just hack in a
# hard-coded zero for the minimal-boot case.
fix_boot_timeout() {
    sed -i -e's,\$(CONFIG_BOOT_DEFAULT_TIMEOUT),0,' ${S}/../edk2-nvidia/Platform/NVIDIA/NVIDIA.common.dsc.inc
}
do_patch[postfuncs] += "${@'fix_boot_timeout' if bb.utils.to_boolean(d.getVar('TEGRA_MINIMAL_BOOT')) else ''}"


do_configure:append() {
    if [ -n "${TEGRA_UEFI_SYSTEM_IMAGE_TYPE_GUID}" ]; then
        echo 'CONFIG_FMP_SYSTEM_IMAGE_TYPE_ID="${TEGRA_UEFI_SYSTEM_IMAGE_TYPE_GUID}"' > ${B}/nvidia-config/Tegra/${EDK2_PLATFORM}/image_type_id_override.cfg
        extracfg=${B}/nvidia-config/Tegra/${EDK2_PLATFORM}/image_type_id_override.cfg
    else
       rm -f ${B}/nvidia-config/Tegra/${EDK2_PLATFORM}/image_type_id_override.cfg
       extracfg=
    fi
    ${PYTHON} ${UNPACKDIR}/nvbuildconfig.py --kconfig-path=${S_EDK2_NVIDIA}/Platform/NVIDIA/Kconfig --output-dir=${B}/nvidia-config/Tegra/${EDK2_PLATFORM} ${S_EDK2_NVIDIA}/Platform/NVIDIA/Tegra/DefConfigs/${EDK2_PLATFORM}.defconfig ${@config_fragments(d)} $extracfg
    . ${B}/nvidia-config/Tegra/${EDK2_PLATFORM}/.config
    echo "$CONFIG_FMP_SYSTEM_IMAGE_TYPE_ID" > ${B}/nvidia-config/Tegra/${EDK2_PLATFORM}/fmp-image-type-id.txt
}

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
    fdtput -t i ${B}/images/L4TConfiguration.dtbo "/fragment@0/__overlay__/firmware/uefi" fmp-lowest-supported-version ${TEGRA_UEFI_LOWEST_SUPPORTED_VERSION}
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
    # For the minimal-boot configuration, L4TLauncher is built directly
    # into the UEFI image, and should not be installed in the ESP.
    if ${@'false' if bb.utils.to_boolean(d.getVar('TEGRA_MINIMAL_BOOT')) else 'true'}; then
        install -m 0644 ${B}/images/BOOTAA64.efi ${D}${EFIDIR}/${EFI_BOOT_IMAGE}
    fi
}

PACKAGES = "l4t-launcher"
FILES:l4t-launcher = "${EFIDIR}"
INSANE_SKIP:l4t-launcher = "buildpaths"

do_deploy() {
    install -d ${DEPLOYDIR}
    install -m 0644 ${B}/images/${EDK2_BIN_NAME} ${DEPLOYDIR}/${TEGRA_FLASHVAR_UEFI_IMAGE}.bin
    install -m 0644 ${B}/nvidia-config/Tegra/${EDK2_PLATFORM}/fmp-image-type-id.txt ${DEPLOYDIR}/${TEGRA_FLASHVAR_UEFI_IMAGE}.fmp-image-type-id
    for dtb in ${TEGRA_BOOTCONTROL_OVERLAYS} L4TConfiguration-rcmboot.dtbo; do
	[ -e ${B}/images/$dtb ] || continue
	install -m 0644 ${B}/images/$dtb ${DEPLOYDIR}/
    done
}
do_deploy[depends] += "${@'l4t-launcher-rootfs-ab-config:do_deploy' if bb.utils.to_boolean(d.getVar('USE_REDUNDANT_FLASH_LAYOUT')) else ''}"

addtask deploy before do_build after do_install
