UBOOT_BINARY ?= "u-boot-dtb-tegra.${UBOOT_SUFFIX}"

require recipes-bsp/u-boot/u-boot.inc

LICENSE = "GPLv2+"
DESCRIPTION = "U-Boot for Nvidia Tegra platforms, based on Nvidia sources"
COMPATIBLE_MACHINE = "(tegra124)"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=025bf9f768cbcb1a165dbe1a110babfb"

PROVIDES += "u-boot"

DEPENDS = "dtc-native"

UBOOT_TEGRA_REPO ?= "github.com/madisongh/u-boot-tegra.git"
SRCBRANCH ?= "patches-l4t-r21.6"
SRC_URI = "git://${UBOOT_TEGRA_REPO};branch=${SRCBRANCH}"
SRCREV = "6e04e5274568847c1cfe7fe7f7e13e7f3386e9dd"
PV .= "+git${SRCPV}"

S = "${WORKDIR}/git"
