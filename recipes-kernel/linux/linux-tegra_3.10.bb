SECTION = "kernel"
SUMMARY = "Linux for Tegra kernel recipe"
DESCRIPTION = "Linux kernel from sources provided by Nvidia for Tegra processors."
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

inherit kernel

require recipes-kernel/linux/linux-dtb.inc

PV .= "+git${SRCPV}"
FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}-${@bb.parse.BBHandler.vars_from_file(d.getVar('FILE', False),d)[1]}:"
DEPENDS_append_aarch64 = " lib32-gcc-cross-arm lib32-gcc-runtime lib32-binutils-cross-arm"
CROSS32TC = ""
CROSS32TC_aarch64 = "${TUNE_ARCH_32}${TARGET_VENDOR_virtclass-multilib-lib32}-${TARGET_OS}-gnu${ABIEXTENSION_32}"
PATH_prepend_aarch64 = "${STAGING_BINDIR_NATIVE}/${CROSS32TC}:"
EXTRA_OEMAKE_append_aarch64 = ' CROSS32CC="${CROSS32TC}-gcc -march=armv7-a -mfloat-abi=hard" CROSS32LD="${CROSS32TC}-ld.bfd"'
EXTRA_OEMAKE += 'LIBGCC=""'

L4T_VERSION = "l4t-r23.1"
LOCALVERSION = "-${L4T_VERSION}"

SRCBRANCH = "patches-${L4T_VERSION}"
SRCREV = "7b27278e4f6ad170024a12b76e477082deb74f7e"
KERNEL_REPO = "github.com/madisongh/linux-tegra.git"
SRC_URI = "git://${KERNEL_REPO};branch=${SRCBRANCH} \
	   file://defconfig \
"
S = "${WORKDIR}/git"

do_configure_prepend() {
    sed -e's,^CONFIG_LOCALVERSION=.*$,CONFIG_LOCALVERSION="${LOCALVERSION}",' < ${WORKDIR}/defconfig > ${B}/.config
    echo "+g${SRCREV}" | cut -c -9 | tee ${S}/.scmversion > ${B}/.scmversion
}

COMPATIBLE_MACHINE = "(jetson-tx1)"
