UBOOT_INITIAL_ENV ?= "u-boot-initial-env"

require recipes-bsp/u-boot/u-boot-common.inc
require recipes-bsp/u-boot/u-boot.inc

COMPATIBLE_MACHINE = "(tegra186|tegra210)"

DEPENDS += "bc-native dtc-native ${SOC_FAMILY}-flashtools-native"

SRC_REPO ?= "github.com/OE4T/u-boot-tegra.git;protocol=https"
SRC_URI = "git://${SRC_REPO};branch=${SRCBRANCH}"
SRCBRANCH ?= "patches-v2021.10"
SRCREV = "5529f7f9ec94d68dd4467e22362a0be346406b93"

PV .= "+g${SRCPV}"

SRC_URI += "\
    file://fw_env.config \
"

EXTRA_OEMAKE += "DTC=dtc"

PROVIDES += "u-boot"

require u-boot-tegra-bootimg.inc

FILES:${PN}-extlinux += "/boot/initrd"
ALLOW_EMPTY:${PN}-extlinux = "1"
RPROVIDES:${PN}-extlinux += "u-boot-extlinux"
RPROVIDES:${PN} += "u-boot"
