LICENSE = "GPLv2+"
DESCRIPTION = "U-Boot for Nvidia Tegra186 platforms, based on Nvidia sources"
COMPATIBLE_MACHINE = "(tegra186|tegra210)"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=a2c678cfd4a4d97135585cad908541c6"

UBOOT_TEGRA_REPO ?= "github.com/madisongh/u-boot-tegra.git"
SRCBRANCH ?= "patches-l4t-r28.2"
SRC_URI = "git://${UBOOT_TEGRA_REPO};branch=${SRCBRANCH}"
SRCREV = "8a044b187228e60b454529e72c0610edd9d845bf"
PV .= "+git${SRCPV}"

S = "${WORKDIR}/git"
