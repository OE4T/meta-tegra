require recipes-bsp/u-boot/u-boot.inc

DESCRIPTION = "U-Boot for Nvidia Tegra platforms, based on Nvidia sources"
COMPATIBLE_MACHINE = "(jetson.*)"

PROVIDES += "u-boot"

SRCBRANCH ?= "l4t/l4t-r23.1"
SRC_URI = "git://nv-tegra.nvidia.com/3rdparty/u-boot.git;branch=${SRCBRANCH}"
SRCREV = "2ac3917df428c7ab636f158c20c538e138abb45d"
PV .= "+git${SRCPV}"