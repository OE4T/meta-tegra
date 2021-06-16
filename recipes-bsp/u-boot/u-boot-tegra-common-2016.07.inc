LICENSE = "GPLv2+"
DESCRIPTION = "U-Boot for Nvidia Tegra186 platforms, based on Nvidia sources"
COMPATIBLE_MACHINE = "(tegra186|tegra210)"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=a2c678cfd4a4d97135585cad908541c6"

#UBOOT_TEGRA_REPO ?= "github.com/madisongh/u-boot-tegra.git"
UBOOT_TEGRA_REPO ?= "github.com/mandraga/u-boot-tegra.git"
SRCBRANCH ?= "patches-l4t-r28.4"
SRC_URI = "git://${UBOOT_TEGRA_REPO};branch=${SRCBRANCH}"
SRCREV = "eb5c15bec518cf07d0a9a92e34967b6a1c0eac31"
PV .= "+git${SRCPV}"

S = "${WORKDIR}/git"
