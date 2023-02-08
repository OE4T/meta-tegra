require edk2-firmware-tegra-35.2.1.inc

DESCRIPTION = "UEFI EDK2 Firmware for Jetson platforms"

PROVIDES = "virtual/bootloader"

DEPENDS += "dtc-native"
DEPENDS:append:tegra194 = " nvdisp-init"

inherit deploy

EDK2_PLATFORM = "Jetson"
EDK2_PLATFORM_DSC = "Platform/NVIDIA/Jetson/Jetson.dsc"
EDK2_BIN_NAME = "uefi_jetson.bin"
NVDISPLAY_INIT_DEFAULT = ""
NVDISPLAY_INIT_DEFAULT:tegra194 = "${DEPLOY_DIR_IMAGE}/nvdisp-init.bin"
NVDISPLAY_INIT ?= "${NVDISPLAY_INIT_DEFAULT}"
NVDISPLAY_INIT_DEPS = ""
NVDISPLAY_INIT_DEPS:tegra194 = "nvdisp-init:do_deploy"

do_compile:append() {
    rm -rf ${B}/images
    mkdir ${B}/images
    python3 ${S_EDK2_NVIDIA}/Silicon/NVIDIA/Tools/FormatUefiBinary.py \
        ${B}/Build/${EDK2_PLATFORM}/${EDK2_BUILD_MODE}_${EDK_COMPILER}/FV/UEFI_NS.Fv \
        ${B}/images/${EDK2_BIN_NAME}.tmp
    if [ -n "${NVDISPLAY_INIT}" ]; then
        cat "${NVDISPLAY_INIT}" ${B}/images/${EDK2_BIN_NAME}.tmp > ${B}/images/${EDK2_BIN_NAME}
	rm ${B}/images/${EDK2_BIN_NAME}.tmp
    else
	mv ${B}/images/${EDK2_BIN_NAME}.tmp ${B}/images/${EDK2_BIN_NAME}
    fi
    python3 ${S_EDK2_NVIDIA}/Silicon/NVIDIA/Tools/FormatUefiBinary.py \
        ${B}/Build/${EDK2_PLATFORM}/${EDK2_BUILD_MODE}_${EDK_COMPILER}/AARCH64/L4TLauncher.efi \
        ${B}/images/BOOTAA64.efi
    for f in ${B}/Build/${EDK2_PLATFORM}/${EDK2_BUILD_MODE}_${EDK_COMPILER}/AARCH64/Silicon/NVIDIA/Tegra/DeviceTree/DeviceTree/OUTPUT/*.dtb; do
	[ -e "$f" ] || continue
	fbase=$(basename "$f" ".dtb")
	cp $f ${B}/images/$fbase.dtbo
    done
    cp ${B}/images/L4TConfiguration.dtbo ${B}/images/L4TConfiguration-rcmboot.dtbo
    fdtput -t s ${B}/images/L4TConfiguration-rcmboot.dtbo /fragment@0/__overlay__/firmware/uefi/variables/gNVIDIATokenSpaceGuid/DefaultBootPriority data boot.img
}
do_compile[depends] += "${NVDISPLAY_INIT_DEPS}"

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
    for f in ${B}/images/*.dtbo; do
	[ -e "$f" ] || continue
	install -m 0644 $f ${DEPLOYDIR}/
    done
}

addtask deploy before do_build after do_install
