require recipes-bsp/tegra-binaries/tegra-binaries-${PV}.inc
require recipes-bsp/tegra-binaries/tegra-shared-binaries.inc
require conf/image-uefi.conf

COMPATIBLE_MACHINE = "(tegra)"
INHIBIT_DEFAULT_DEPS = "1"

PROVIDES = "virtual/bootloader"

inherit deploy

do_install() {
    install -d ${D}${EFIDIR}
    install -m 0644 ${S}/bootloader/BOOTAA64.efi ${D}${EFIDIR}/${EFI_BOOT_IMAGE}
}

do_deploy() {
    install -d ${DEPLOYDIR}
    install -m 0644 ${S}/bootloader/uefi_jetson.bin ${DEPLOYDIR}/
}

PACKAGES = "l4t-launcher-prebuilt"
RPROVIDES:l4t-launcher-prebuilt = "l4t-launcher"
RCONFLICTS:l4t-launcher-prebuilt = "l4t-launcher"
RREPLACES:l4t-launcher-prebuilt = "l4t-launcher"
FILES:l4t-launcher-prebuilt = "${EFIDIR}"

PACKAGE_ARCH = "${MACHINE_ARCH}"

addtask deploy before do_build after do_install
