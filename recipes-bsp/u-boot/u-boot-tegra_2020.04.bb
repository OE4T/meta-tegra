UBOOT_BINARY ?= "u-boot-dtb.${UBOOT_SUFFIX}"

require recipes-bsp/u-boot/u-boot.inc
require u-boot-tegra-common-${PV}.inc

PROVIDES += "u-boot"
DEPENDS += "dtc-native bc-native bison-native ${SOC_FAMILY}-flashtools-native"

require u-boot-tegra-bootimg.inc

RPROVIDES_${PN} += "u-boot"
