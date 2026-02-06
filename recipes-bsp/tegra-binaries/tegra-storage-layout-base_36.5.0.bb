require tegra-binaries-${PV}.inc
require tegra-shared-binaries.inc

COMPATIBLE_MACHINE = "(tegra)"
INHIBIT_DEFAULT_DEPS = "1"

PARTITION_FILE ?= "${S}/bootloader/${NVIDIA_BOARD}/cfg/${PARTITION_LAYOUT_TEMPLATE}"
PARTITION_FILE_EXTERNAL ?= "${S}/tools/kernel_flash/${PARTITION_LAYOUT_EXTERNAL}"

do_install() {
    install -d ${D}${datadir}/l4t-storage-layout
    install -m 0644 ${PARTITION_FILE} ${D}${datadir}/l4t-storage-layout/${PARTITION_LAYOUT_TEMPLATE}
    [ -z "${PARTITION_LAYOUT_EXTERNAL}" ] || install -m 0644 ${PARTITION_FILE_EXTERNAL} ${D}${datadir}/l4t-storage-layout/${PARTITION_LAYOUT_EXTERNAL}
}

PACKAGES = "${PN}-dev"
FILES:${PN}-dev = "${datadir}"
RRECOMMENDS:${PN}-dev = ""
PACKAGE_ARCH = "${MACHINE_ARCH}"
