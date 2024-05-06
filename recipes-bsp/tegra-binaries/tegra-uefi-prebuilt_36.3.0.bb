require recipes-bsp/tegra-binaries/tegra-binaries-${PV}.inc
require recipes-bsp/tegra-binaries/tegra-shared-binaries.inc
require conf/image-uefi.conf

COMPATIBLE_MACHINE = "(tegra)"
INHIBIT_DEFAULT_DEPS = "1"

PROVIDES = "virtual/bootloader standalone-mm-optee-tegra"

DEPENDS = "coreutils-native dtc-native"

TEGRA_UEFI_SIGNING_CLASS ??= "tegra-uefi-signing"

inherit deploy ${TEGRA_UEFI_SIGNING_CLASS}

do_compile() {
    cp ${S}/bootloader/uefi_jetson.bin ${S}/bootloader/BOOTAA64.efi ${B}
}

do_compile:append:tegra234() {
    cp ${S}/bootloader/standalonemm_optee_t234.bin ${B}/standalone_mm_optee.bin
}

sign_efi_app() {
    tegra_uefi_sbsign "$1"
}

do_sign_efi_launcher() {
    sign_efi_app BOOTAA64.efi
}
do_sign_efi_launcher[dirs] = "${B}"
do_sign_efi_launcher[depends] += "${TEGRA_UEFI_SIGNING_TASKDEPS}"

addtask sign_efi_launcher after do_compile before do_install

do_install() {
    install -d ${D}${EFIDIR}
    install -m 0644 ${B}/BOOTAA64.efi ${D}${EFIDIR}/${EFI_BOOT_IMAGE}
    install -d ${D}${datadir}/edk2-nvidia
    install -m 0644 ${B}/standalone_mm_optee.bin ${D}${datadir}/edk2-nvidia/
}

do_deploy() {
    install -d ${DEPLOYDIR}
    install -m 0644 ${B}/uefi_jetson.bin ${DEPLOYDIR}/
    for dtbo in ${TEGRA_BOOTCONTROL_OVERLAYS}; do
	[ -e ${S}/kernel/dtb/$dtbo ] || continue
	install -m 0644 ${S}/kernel/dtb/$dtbo ${DEPLOYDIR}/
    done
    install -m 0644 ${S}/kernel/dtb/L4TConfiguration.dtbo ${DEPLOYDIR}/L4TConfiguration-rcmboot.dtbo
    fdtput -t s ${DEPLOYDIR}/L4TConfiguration-rcmboot.dtbo /fragment@0/__overlay__/firmware/uefi/variables/gNVIDIATokenSpaceGuid/DefaultBootPriority data boot.img
}
do_deploy[depends] += "${@'l4t-launcher-rootfs-ab-config:do_deploy' if bb.utils.to_boolean(d.getVar('USE_REDUNDANT_FLASH_LAYOUT')) else ''}"

PACKAGES = "l4t-launcher-prebuilt standalone-mm-optee-tegra-prebuilt"
RPROVIDES:l4t-launcher-prebuilt = "l4t-launcher"
RCONFLICTS:l4t-launcher-prebuilt = "l4t-launcher"
RREPLACES:l4t-launcher-prebuilt = "l4t-launcher"
FILES:l4t-launcher-prebuilt = "${EFIDIR}"
RPROVIDES:standalone-mm-optee-tegra-prebuilt = "standalone-mm-optee-tegra"
RREPLACES:standalone-mm-optee-tegra-prebuilt = "standalone-mm-optee-tegra"
RCONFLICTS:standalone-mm-optee-tegra-prebuilt = "standalone-mm-optee-tegra"
FILES:standalone-mm-optee-tegra-prebuilt = "${datadir}/edk2-nvidia"
PACKAGE_ARCH = "${MACHINE_ARCH}"

addtask deploy before do_build after do_install
