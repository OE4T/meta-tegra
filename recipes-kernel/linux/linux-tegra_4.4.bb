SECTION = "kernel"
SUMMARY = "Linux for Tegra kernel recipe"
DESCRIPTION = "Linux kernel from sources provided by Nvidia for Tegra processors."
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://COPYING;md5=d7810fab7487fb0aad327b76f1be7cd7"

inherit kernel

PV .= "+git${SRCPV}"
FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}-${@bb.parse.BBHandler.vars_from_file(d.getVar('FILE', False),d)[1]}:"
EXTRA_OEMAKE += 'LIBGCC=""'

L4T_VERSION = "l4t-r28.2"
SCMVERSION ??= "y"
export LOCALVERSION = ""

SRCBRANCH = "patches-${L4T_VERSION}"
SRCREV = "bd1881feaed0a2b83d0b4ac3612db677a0f76b05"
KERNEL_REPO = "github.com/madisongh/linux-tegra.git"
SRC_URI = "git://${KERNEL_REPO};branch=${SRCBRANCH} \
           file://defconfig \
           file://0001-give-up-on-gcc-ilog2-constant-optimizations.patch \
           file://0002-Kbuild-suppress-packed-not-aligned-warning-for-defau.patch \
           file://0003-kbuild-don-t-warn-on-Wattribute-alias.patch \
           file://0004-kbuild-don-t-warn-on-Wstringop-truncation.patch \
           file://0005-kbuild-don-t-warn-on-Wstringop-overflow.patch \
           "
S = "${WORKDIR}/git"

KERNEL_ROOTSPEC ?= "root=/dev/mmcblk\${devnum}p1 rw rootwait"

do_configure_prepend() {
    localversion="-${L4T_VERSION}"
    if [ "${SCMVERSION}" = "y" ]; then
	head=`git --git-dir=${S}/.git rev-parse --verify --short HEAD 2> /dev/null`
        [ -z "$head" ] || localversion="${localversion}+g${head}"
    fi
    sed -e"s,^CONFIG_LOCALVERSION=.*$,CONFIG_LOCALVERSION=\"${localversion}\"," \
	< ${WORKDIR}/defconfig > ${B}/.config
}

do_install_append() {
    if [ "${TEGRA_INITRAMFS_INITRD}" = "1" ]; then
        install -m 0644 ${DEPLOY_DIR_IMAGE}/${INITRAMFS_IMAGE_NAME}.cpio.gz ${D}/${KERNEL_IMAGEDEST}/initrd
    fi
}
do_install[depends] += "${@['', '${INITRAMFS_IMAGE}:do_image_complete'][(d.getVar('INITRAMFS_IMAGE', True) or '') != '' and (d.getVar('TEGRA_INITRAMFS_INITRD', True) or '') == "1"]}"

KERNEL_ARGS ?= "\${cbootargs}"

generate_extlinux_conf() {
    install -d ${D}/${KERNEL_IMAGEDEST}/extlinux
    rm -f ${D}/${KERNEL_IMAGEDEST}/extlinux/extlinux.conf
    cat >${D}/${KERNEL_IMAGEDEST}/extlinux/extlinux.conf << EOF
DEFAULT primary
TIMEOUT 30
MENU TITLE Boot Options
LABEL primary
      MENU LABEL primary ${KERNEL_IMAGETYPE}-${KERNEL_VERSION}
      LINUX /${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}-${KERNEL_VERSION}
      APPEND ${KERNEL_ARGS} ${KERNEL_ROOTSPEC}
EOF
    if [ -n "${INITRAMFS_IMAGE}" -a "${TEGRA_INITRAMFS_INITRD}" = "1" ]; then
        echo "      INITRD /${KERNEL_IMAGEDEST}/initrd" >> ${D}/${KERNEL_IMAGEDEST}/extlinux/extlinux.conf
    fi
}

do_install[postfuncs] += "generate_extlinux_conf"

FILES_${KERNEL_PACKAGE_NAME}-image += "/${KERNEL_IMAGEDEST}/extlinux /${KERNEL_IMAGEDEST}/initrd"

COMPATIBLE_MACHINE = "(tegra186|tegra210)"
