LICENSE = "GPLv2+"
DESCRIPTION = "U-Boot for Nvidia Tegra186 platforms, based on Nvidia sources"
COMPATIBLE_MACHINE = "(tegra186|tegra210)"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=a2c678cfd4a4d97135585cad908541c6"

UBOOT_TEGRA_REPO ?= "github.com/madisongh/u-boot-tegra.git"
SRCBRANCH ?= "patches-l4t-r28.2"
SRC_URI = "git://${UBOOT_TEGRA_REPO};branch=${SRCBRANCH}"
SRCREV = "70a477d24c42d7a4131570ff5058cd70ee076f1b"
PV .= "+git${SRCPV}"

S = "${WORKDIR}/git"
