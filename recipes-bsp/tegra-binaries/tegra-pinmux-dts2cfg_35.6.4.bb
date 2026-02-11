# Recipe for providing the pinmux-dts2cfg.py script for converting nvidia dtsi
# files to nvidia cfg files - used later for minimal initramfs for pin setup

require recipes-bsp/tegra-binaries/tegra-binaries-${PV}.inc

DESCRIPTION = "pixmux-dts2cfg for converting dtsi to cfg - used on xavier"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

# Being a python tool used on native host, we need to clear COMPATIBLE_MACHINE
COMPATIBLE_MACHINE = ""

# Normally used as native package E.g. convert the dtsi to cfg on host, but
# support target install also.
BBCLASSEXTEND = "native nativesdk"

# Need to setup workdir like set in include file after extending to native
# Also see tegra-flashtools-native for similar setup.
WORKDIR = "${TMPDIR}/work-shared/L4T-${L4T_BSP_ARCH}-${PV}-${PR}"
SSTATE_SWSPEC = "sstate:tegra-binaries-${L4T_BSP_ARCH}::${PV}:${PR}::${SSTATE_VERSION}:"
STAMP = "${STAMPS_DIR}/work-shared/L4T-${L4T_BSP_ARCH}-${PV}-${PR}"
STAMPCLEAN = "${STAMPS_DIR}/work-shared/L4T-${L4T_BSP_ARCH}-${PV}-*"

S = "${TMPDIR}/work-shared/L4T-${L4T_BSP_ARCH}-${PV}-${PR}/Linux_for_Tegra"

do_configure[noexec] = "1"
do_compile[noexec] = "1"

do_install() {
    install -d ${D}${bindir}
    install -m 0755 ${S}/kernel/pinmux/t19x/pinmux-dts2cfg.py ${D}${bindir}/

    install -d ${D}${datadir}/tegra-pinmux/
    install -m 0644 ${S}/kernel/pinmux/t19x/addr_info.txt ${D}${datadir}/tegra-pinmux/
    install -m 0644 ${S}/kernel/pinmux/t19x/gpio_addr_info.txt ${D}${datadir}/tegra-pinmux/
    install -m 0644 ${S}/kernel/pinmux/t19x/mandatory_pinmux.txt ${D}${datadir}/tegra-pinmux/
    install -m 0644 ${S}/kernel/pinmux/t19x/pad_info.txt ${D}${datadir}/tegra-pinmux/
    install -m 0644 ${S}/kernel/pinmux/t19x/por_val.txt ${D}${datadir}/tegra-pinmux/
}
