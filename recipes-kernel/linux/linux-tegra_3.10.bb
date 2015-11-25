SECTION = "kernel"
SUMMARY = "Linux for Tegra kernel recipe"
DESCRIPTION = "Linux kernel from sources provided by Nvidia for Tegra processors."
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

inherit kernel

require recipes-kernel/linux/linux-dtb.inc

L4T_VERSION = "l4t-r23.1"
LOCALVERSION = "-${L4T_VERSION}"

SRCBRANCH = "l4t/${L4T_VERSION}"
SRCREV = "cdddc523aafea574cd2d83d6f3455ae403b27354"
SRC_URI = "git://nv-tegra.nvidia.com/linux-3.10.git;branch=${SRCBRANCH} \
	   file://defconfig \
"
S = "${WORKDIR}/git"

do_configure_prepend() {
    sed -e's,^CONFIG_LOCALVERSION=.*$,CONFIG_LOCALVERSION="${LOCALVERSION}",' < ${WORKDIR}/defconfig > ${B}/.config
    echo "+g${SRCREV}" | cut -c -9 | tee ${S}/.scmversion > ${B}/.scmversion
}

do_compile_prepend() {
    export CROSS32CC="tbd"
    export CROSS32LD="tbd"
}
COMPATIBLE_MACHINE = "(jetson-tx1)"
