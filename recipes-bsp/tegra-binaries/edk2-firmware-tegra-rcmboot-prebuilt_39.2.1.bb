require recipes-bsp/tegra-binaries/tegra-binaries-${PV}.inc
require recipes-bsp/tegra-binaries/tegra-shared-binaries.inc

COMPATIBLE_MACHINE = "(tegra)"
INHIBIT_DEFAULT_DEPS = "1"

PROVIDES = "edk2-firmware-tegra-rcmboot"

TEGRA_EDK2_PLATFORM ??= "UNKNOWN"
TEGRA_EDK2_CONFIGURATION = "minimal"
EDK2_PLATFORM = "${TEGRA_EDK2_PLATFORM}_${TEGRA_EDK2_CONFIGURATION}"
TEGRA_FLASHVAR_UEFI_IMAGE ??= "uefi_${EDK2_PLATFORM}"
EDK2_BIN_NAME = "uefi_${EDK2_PLATFORM}.bin"

inherit deploy

do_compile(){
    if [ "${SOC_FAMILY}" = "tegra234" ]; then
        cp ${S}/bootloader/uefi_bins/uefi_t23x_*.bin ${B}
    elif [ "${SOC_FAMILY}" = "tegra264" ]; then
        cp ${S}/bootloader/uefi_bins/uefi_t26x_*.bin ${B}
    fi
}

do_install() {
    :
}

do_deploy() {
    install -d ${DEPLOYDIR}
    install -m 0644 ${B}/${EDK2_BIN_NAME} ${DEPLOYDIR}/${TEGRA_FLASHVAR_RCM_UEFI_IMAGE}.bin
}
do_deploy[depends] += "virtual/bootloader:do_deploy"

addtask deploy before do_build after do_install

RPROVIDES:${PN} = "edk2-firmware-tegra-rcmboot"
RREPLACES:${PN} = "edk2-firmware-tegra-rcmboot"
RCONFLICTS:${PN} = "edk2-firmware-tegra-rcmboot"
PACKAGE_ARCH = "${MACHINE_ARCH}"

addtask deploy before do_build after do_install
