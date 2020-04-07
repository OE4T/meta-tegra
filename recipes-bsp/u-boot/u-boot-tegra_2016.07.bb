UBOOT_BINARY ?= "u-boot-dtb.${UBOOT_SUFFIX}"

require u-boot.inc
require u-boot-tegra-common-${PV}.inc

SECTION = "bootloaders"
PE = "1"

B = "${WORKDIR}/build"
do_configure[cleandirs] = "${B}"

PROVIDES += "u-boot"
DEPENDS += "flex-native bison-native"

require u-boot-tegra-bootimg.inc

RPROVIDES_${PN} += "u-boot"
