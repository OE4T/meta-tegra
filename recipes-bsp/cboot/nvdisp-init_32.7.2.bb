DESCRIPTION = "nvdisp-init based on cboot bootloader for Tegra194"

L4T_VERSION = "${PV}"
L4T_BSP_NAME = "sources/T186"
inherit l4t_bsp

SRC_URI = "${L4T_URI_BASE}/cboot_src_t19x.tbz2;downloadfilename=cboot_src_t19x-${PV}.tbz2;subdir=${BP} \
           file://0001-Drop-mistaken-global-variable-definition-in-sdmmc_de.patch \
           file://0002-Convert-Python-scripts-to-Python3.patch \
           file://0003-macros.mk-fix-GNU-make-4.3-compatibility.patch \
           file://0004-Restore-version-number-to-L4T-builds.patch \
           file://0005-nvdisp-init.patch \
           "


SRC_URI[sha256sum] = "76e1105810bd5827facea9547e25bcc7a9cd4e1dda56a2b1c19c687b65c27f93"

DEPENDS += "coreutils-native"
TARGET_SOC = "t194"
COMPATIBLE_MACHINE = "(tegra194)"
PROVIDES += "nvdisp-init"
CBOOT_SYMLINK = "nvdisp-init.bin"

require cboot-l4t.inc

do_compile:append() {
    truncate --size=393216 ${B}/${CBOOT_BUILD_ARTIFACT}
}
