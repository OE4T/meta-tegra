require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

COMPATIBLE_MACHINE = "(tegra124)"
INHIBIT_DEFAULT_DEPS = "1"

BCT_TEMPLATE ?= "${S}/bootloader/${NVIDIA_BOARD}/BCT/${EMMC_BCT}"
PARTITION_FILE ?= "${S}/bootloader/${NVIDIA_BOARD}/cfg/${PARTITION_LAYOUT_TEMPLATE}"

do_compile[noexec] = "1"

do_install() {
    install -d ${D}${datadir}/tegraflash
    install -m 0644 ${S}/bootloader/${NVIDIA_BOARD}/fastboot.bin ${D}${datadir}/tegraflash
    install -m 0644 ${BCT_TEMPLATE} ${D}${datadir}/tegraflash/${MACHINE}.cfg
    install -m 0644 ${PARTITION_FILE} ${D}${datadir}/tegraflash/flash_${MACHINE}.cfg
}

PACKAGES = "${PN}-dev"
FILES_${PN}-dev = "${datadir}"
RDEPENDS_${PN}-dev = ""
PACKAGE_ARCH = "${MACHINE_ARCH}"
