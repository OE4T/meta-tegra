require recipes-bsp/tegra-binaries/tegra-binaries-${PV}.inc
require recipes-bsp/tegra-binaries/tegra-shared-binaries.inc

COMPATIBLE_MACHINE = "(tegra)"
INHIBIT_DEFAULT_DEPS = "1"

PROVIDES = "edk2-nvidia-standalone-mm"

inherit deploy

do_compile(){
    :
}

do_compile:append:tegra234() {
    cp ${S}/bootloader/standalonemm_optee_t234.bin ${B}/standalone_mm_optee.bin
}

do_compile:append:tegra264() {
    cp ${S}/bootloader/standalonemm_jetson.pkg ${B}/standalonemm_jetson.pkg
}

do_install() {
    if [ "${SOC_FAMILY}" = "tegra234" ]; then
        install -D -m 0644 -t ${D}${datadir}/edk2-nvidia ${B}/standalone_mm_optee.bin
    elif [ "${SOC_FAMILY}" = "tegra264" ]; then
        install -D -m 0644 -t ${D}${datadir}/edk2-nvidia ${B}/standalonemm_jetson.pkg
    fi
}

do_deploy() {
    install -d ${DEPLOYDIR}
    if [ "${SOC_FAMILY}" = "tegra264" ]; then
        install -m 0644 ${B}/standalonemm_jetson.pkg ${DEPLOYDIR}/
    fi
}

RPROVIDES:${PN} = "edk2-nvidia-standalone-mm"
RREPLACES:${PN} = "edk2-nvidia-standalone-mm"
RCONFLICTS:${PN} = "edk2-nvidia-standalone-mm"
FILES:${PN} = "${datadir}/edk2-nvidia"
PACKAGE_ARCH = "${MACHINE_ARCH}"

addtask deploy before do_build after do_install
