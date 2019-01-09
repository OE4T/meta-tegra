UBOOT_BINARY ?= "u-boot-dtb-tegra.${UBOOT_SUFFIX}"

require recipes-bsp/u-boot/u-boot.inc

LICENSE = "GPLv2+"
DESCRIPTION = "U-Boot for Nvidia Tegra platforms, based on Nvidia sources"
COMPATIBLE_MACHINE = "(tegra124)"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=025bf9f768cbcb1a165dbe1a110babfb"

PROVIDES += "u-boot"

DEPENDS = "dtc-native"

UBOOT_TEGRA_REPO ?= "github.com/madisongh/u-boot-tegra.git"
SRCBRANCH ?= "patches-l4t-r21.7"
SRC_URI = "git://${UBOOT_TEGRA_REPO};branch=${SRCBRANCH}"
SRCREV = "61c56f7e658c2f330197f196f656cc61b6458d6d"
PV .= "+git${SRCPV}"

S = "${WORKDIR}/git"

do_configure() {
    if [ -z "${UBOOT_CONFIG}" ]; then
	if [ -n "${UBOOT_MACHINE}" ]; then
	    oe_runmake -C ${S} O=${B} ${UBOOT_MACHINE}
	else
	    oe_runmake -C ${S} O=${B} oldconfig
	fi
	${S}/scripts/kconfig/merge_config.sh -m .config ${@" ".join(find_cfgs(d))}
	cml1_do_configure
    fi
}
