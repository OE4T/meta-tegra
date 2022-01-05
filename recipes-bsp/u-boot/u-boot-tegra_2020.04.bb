UBOOT_INITIAL_ENV ?= "u-boot-initial-env"

require recipes-bsp/u-boot/u-boot-common.inc
require recipes-bsp/u-boot/u-boot.inc

COMPATIBLE_MACHINE = "(tegra186|tegra210)"

DEPENDS += "bc-native dtc-native ${SOC_FAMILY}-flashtools-native"

SRC_REPO ?= "github.com/OE4T/u-boot-tegra.git;protocol=https"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCBRANCH ?= "patches-v2020.04"
SRCREV = "acd972975c8c5626c7c411be72c094d1e5bff272"

PV .= "+g${SRCPV}"

SRC_URI += "\
    file://fw_env.config \
"

EXTRA_OEMAKE += "DTC=dtc"

PROVIDES += "u-boot"

require u-boot-tegra-bootimg.inc

PACKAGES =+ "${PN}-extlinux"
FILES_${PN}-extlinux = "/boot/extlinux /boot/initrd"
ALLOW_EMPTY_${PN}-extlinux = "1"
RPROVIDES_${PN}-extlinux += "u-boot-extlinux"
RPROVIDES_${PN} += "u-boot"
RDEPENDS_${PN} += "${PN}-extlinux"
