UBOOT_INITIAL_ENV ?= "u-boot-initial-env"

require recipes-bsp/u-boot/u-boot-common.inc
require recipes-bsp/u-boot/u-boot.inc

COMPATIBLE_MACHINE = "(tegra186|tegra210)"

DEPENDS += "bc-native dtc-native ${SOC_FAMILY}-flashtools-native"

SRC_REPO ?= "github.com/OE4T/u-boot-tegra.git;protocol=https"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCBRANCH ?= "patches-v2020.04-l4t-r32.4.3"
SRCREV = "31814111b144ea6feeb6ec96818a48528ea31807"

PV .= "+g${SRCPV}"

SRC_URI += "\
    file://fw_env.config \
"

PROVIDES += "u-boot"

require u-boot-tegra-bootimg.inc

PACKAGES =+ "${PN}-extlinux"
FILES_${PN}-extlinux = "/boot/extlinux /boot/initrd"
ALLOW_EMPTY_${PN}-extlinux = "1"
RPROVIDES_${PN}-extlinux += "u-boot-extlinux"
RPROVIDES_${PN} += "u-boot"
RDEPENDS_${PN} += "${PN}-extlinux"
