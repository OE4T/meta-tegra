require recipes-bsp/tegra-binaries/tegra-binaries-${PV}.inc
require recipes-bsp/tegra-binaries/tegra-shared-binaries.inc
require conf/image-uefi.conf

COMPATIBLE_MACHINE = "(tegra)"
INHIBIT_DEFAULT_DEPS = "1"

PROVIDES = "virtual/bootloader"

DEPENDS = "coreutils-native"

DTB_OVERLAYS = "\
   AcpiBoot.dtbo \
   L4TConfiguration.dtbo \
   L4TRootfsInfo.dtbo \
   L4TRootfsABInfo.dtbo \
   L4TRootfsBrokenInfo.dtbo \
"

inherit deploy

do_compile() {
    cp ${S}/bootloader/uefi_jetson.bin ${B}
}

do_compile:tegra194() {
    cp ${S}/bootloader/nvdisp-init.bin ${B}
    truncate --size=393216 ${B}/nvdisp-init.bin
    cat ${B}/nvdisp-init.bin ${S}/bootloader/uefi_jetson.bin > ${B}/uefi_jetson.bin
}

do_install() {
    install -d ${D}${EFIDIR}
    install -m 0644 ${S}/bootloader/BOOTAA64.efi ${D}${EFIDIR}/${EFI_BOOT_IMAGE}
}

do_deploy() {
    install -d ${DEPLOYDIR}
    install -m 0644 ${B}/uefi_jetson.bin ${DEPLOYDIR}/
    for dtbo in ${DTB_OVERLAYS}; do
	install -m 0644 ${S}/kernel/dtb/$dtbo ${DEPLOYDIR}/
    done
}

PACKAGES = "l4t-launcher-prebuilt"
RPROVIDES:l4t-launcher-prebuilt = "l4t-launcher"
RCONFLICTS:l4t-launcher-prebuilt = "l4t-launcher"
RREPLACES:l4t-launcher-prebuilt = "l4t-launcher"
FILES:l4t-launcher-prebuilt = "${EFIDIR}"

PACKAGE_ARCH = "${MACHINE_ARCH}"

addtask deploy before do_build after do_install
