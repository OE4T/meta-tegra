# This file is shared with fw-utils and u-boot builds
LICENSE = "GPLv2+"
DESCRIPTION = "U-Boot for Nvidia Tegra platforms, based on Nvidia sources"
COMPATIBLE_MACHINE = "(tegra186|tegra210)"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=a2c678cfd4a4d97135585cad908541c6"

UBOOT_TEGRA_REPO ?= "github.com/OE4T/u-boot-tegra.git"
SRCBRANCH ?= "patches-l4t-r32.3.1-v2016.07"
SRC_URI = "git://${UBOOT_TEGRA_REPO};branch=${SRCBRANCH}"
SRCREV = "565a18c42c8389916639f8225163942b559bb5f2"
PV .= "+git${SRCPV}"

S = "${WORKDIR}/git"

require u-boot-tegra-extlinux.inc
