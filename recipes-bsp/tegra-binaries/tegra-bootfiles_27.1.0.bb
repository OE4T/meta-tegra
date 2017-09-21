require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

COMPATIBLE_MACHINE = "(jetsontx2)"
INHIBIT_DEFAULT_DEPS = "1"

BCT_TEMPLATE ?= "${S}/bootloader/${NVIDIA_BOARD}/BCT/${EMMC_BCT}"
PARTITION_FILE ?= "${S}/bootloader/${NVIDIA_BOARD}/cfg/${PARTITION_LAYOUT_TEMPLATE}"

BOOTBINS = "\
    bpmp.bin \
    camera-rtcpu-sce.bin \
    cboot.bin \
    eks.img \
    mb1_prod.bin \
    mb1_recovery_prod.bin \
    mce_mts_d15_prod_cr.bin \
    nvtboot_cpu.bin \
    nvtboot_recovery.bin \
    nvtboot_recovery_cpu.bin \
    preboot_d15_prod_cr.bin \
    spe.bin \
    tos.img \
"

BOOTBINS_MACHINE_SPECIFIC = "\
    nvtboot.bin \
    warmboot.bin \
"

do_compile[noexec] = "1"

do_install() {
    install -d ${D}${datadir}/tegraflash
    for f in ${BOOTBINS}; do
        install -m 0644 ${S}/bootloader/$f ${D}${datadir}/tegraflash
    done
    for f in ${BOOTBINS_MACHINE_SPECIFIC}; do
        install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/$f ${D}${datadir}/tegraflash
    done
    install -m 0644 ${BCT_TEMPLATE} ${D}${datadir}/tegraflash/${MACHINE}.cfg
    install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/BCT/tegra186* ${D}${datadir}/tegraflash/
    install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/tegra186-a02-bpmp*dtb ${D}${datadir}/tegraflash/
    install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/BCT/minimal_scr.cfg ${D}${datadir}/tegraflash/
    install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/BCT/emmc.cfg ${D}${datadir}/tegraflash/
    install -m 0644 ${PARTITION_FILE} ${D}${datadir}/tegraflash/flash_${MACHINE}.xml
}

PACKAGES = "${PN}-dev"
FILES_${PN}-dev = "${datadir}"
RDEPENDS_${PN}-dev = ""
PACKAGE_ARCH = "${MACHINE_ARCH}"
