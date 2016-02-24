require recipes-bsp/u-boot/u-boot.inc

DESCRIPTION = "U-Boot for Nvidia Tegra platforms, based on Nvidia sources"
COMPATIBLE_MACHINE = "(jetson.*)"

PROVIDES += "u-boot"

SRCBRANCH ?= "l4t/l4t-r23.2"
SRC_URI = "git://nv-tegra.nvidia.com/3rdparty/u-boot.git;branch=${SRCBRANCH}"
SRCREV = "eea3f71692e6cef01511467c443c7fbfeec0c82a"
PV .= "+git${SRCPV}"