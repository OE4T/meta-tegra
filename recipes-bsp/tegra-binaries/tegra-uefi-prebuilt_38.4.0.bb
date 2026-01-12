require recipes-bsp/tegra-binaries/tegra-binaries-${PV}.inc
require recipes-bsp/tegra-binaries/tegra-shared-binaries.inc
require conf/image-uefi.conf

COMPATIBLE_MACHINE = "(tegra)"
INHIBIT_DEFAULT_DEPS = "1"

PROVIDES = "virtual/bootloader"

DEPENDS = "coreutils-native dtc-native"

TEGRA_UEFI_SIGNING_CLASS ??= "tegra-uefi-signing"

inherit deploy ${TEGRA_UEFI_SIGNING_CLASS}

do_compile(){
    cp ${S}/bootloader/BOOTAA64.efi ${B}
    if [ "${SOC_FAMILY}" = "tegra234" ]; then
        cp ${S}/bootloader/uefi_bins/uefi_t23x_general.bin ${B}
        cp ${S}/bootloader/uefi_bins/uefi_t23x_minimal.bin ${B}
    elif [ "${SOC_FAMILY}" = "tegra264" ]; then
        cp ${S}/bootloader/uefi_bins/uefi_t26x_general.bin ${B}
        cp ${S}/bootloader/uefi_bins/uefi_t26x_minimal.bin ${B}
    fi
}

sign_efi_app() {
    tegra_uefi_sbsign "$1"
}

do_sign_efi_launcher() {
    sign_efi_app BOOTAA64.efi
}
do_sign_efi_launcher[dirs] = "${B}"
do_sign_efi_launcher[depends] += "${TEGRA_UEFI_SIGNING_TASKDEPS}"
do_sign_efi_launcher[file-checksums] += "${TEGRA_UEFI_SIGNING_FILECHECKSUMS}"

addtask sign_efi_launcher after do_compile before do_install

do_install() {
    install -d ${D}${EFIDIR}
    install -m 0644 ${B}/BOOTAA64.efi ${D}${EFIDIR}/${EFI_BOOT_IMAGE}
}

do_deploy() {
    install -d ${DEPLOYDIR}
    if [ "${SOC_FAMILY}" = "tegra234" ]; then
        install -m 0644 ${B}/uefi_t23x_general.bin ${B}/uefi_t23x_minimal.bin ${DEPLOYDIR}/
    elif [ "${SOC_FAMILY}" = "tegra264" ]; then
        install -m 0644 ${B}/uefi_t26x_general.bin ${B}/uefi_t26x_minimal.bin ${DEPLOYDIR}/
    fi
    for dtbo in ${TEGRA_BOOTCONTROL_OVERLAYS}; do
	[ -e ${S}/kernel/dtb/$dtbo ] || continue
	install -m 0644 ${S}/kernel/dtb/$dtbo ${DEPLOYDIR}/
    done
    install -m 0644 ${S}/kernel/dtb/L4TConfiguration.dtbo ${DEPLOYDIR}/L4TConfiguration-rcmboot.dtbo
    fdtput -t s ${DEPLOYDIR}/L4TConfiguration-rcmboot.dtbo /fragment@0/__overlay__/firmware/uefi/variables/gNVIDIATokenSpaceGuid/DefaultBootPriority data boot.img
}
do_deploy[depends] += "${@'l4t-launcher-rootfs-ab-config:do_deploy' if bb.utils.to_boolean(d.getVar('USE_REDUNDANT_FLASH_LAYOUT')) else ''}"

PACKAGES = "l4t-launcher-prebuilt"
RPROVIDES:l4t-launcher-prebuilt = "l4t-launcher"
RCONFLICTS:l4t-launcher-prebuilt = "l4t-launcher"
RREPLACES:l4t-launcher-prebuilt = "l4t-launcher"
FILES:l4t-launcher-prebuilt = "${EFIDIR}"
PACKAGE_ARCH = "${MACHINE_ARCH}"

addtask deploy before do_build after do_install
