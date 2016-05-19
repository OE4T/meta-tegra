require recipes-bsp/u-boot/u-boot.inc

DESCRIPTION = "U-Boot for Nvidia Tegra platforms, based on Nvidia sources"
COMPATIBLE_MACHINE = "(jetson.*)"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=0507cd7da8e7ad6d6701926ec9b84c95"

PROVIDES += "u-boot"

SRCBRANCH ?= "l4t/l4t-r24.1"
SRC_URI = "git://nv-tegra.nvidia.com/3rdparty/u-boot.git;branch=${SRCBRANCH}"
SRCREV = "c19a2f8cba8203612a879b4e5f88fa6a7122362d"
PV .= "+git${SRCPV}"