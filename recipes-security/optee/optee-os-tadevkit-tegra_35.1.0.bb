SUMMARY = "OP-TEE Trusted OS TA devkit"
DESCRIPTION = "OP-TEE TA devkit for build TAs"
HOMEPAGE = "https://www.op-tee.org/"

LICENSE = "BSD-2-Clause & Proprietary"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=2f6a2cb48b5cc5cd0bd3f87a836cb407 \
    file://LICENSE.NVIDIA;md5=ba16bc74328d76e24af960ba01c937dc \
"
FILESEXTRAPATHS:prepend := "${THISDIR}/optee-os-tegra:"

inherit python3native
require optee-tegra.inc

DEPENDS = "python3-pyelftools-native python3-cryptography-native"

SRC_URI += "file://0001-work-around-to-fix-ld.bfd-warning.patch"

S = "${WORKDIR}/optee/optee_os"
B = "${WORKDIR}/build"

EXTRA_OEMAKE += " \
    CFLAGS32='--sysroot=${STAGING_DIR_HOST}' \
    CFLAGS64='--sysroot=${STAGING_DIR_HOST}' \
    PLATFORM='tegra' \
    PLATFORM_FLAVOR='${OPTEE_NV_PLATFORM}' \
    CROSS_COMPILE64='${HOST_PREFIX}' \
    PYTHON3='${PYTHON}' \
    NV_CCC_PREBUILT='${NV_CCC_PREBUILT}' \
    O='${B}' \
"

CFLAGS[unexport] = "1"
LDFLAGS[unexport] = "1"
CPPFLAGS[unexport] = "1"
AS[unexport] = "1"
LD[unexport] = "1"

do_configure[noexec] = "1"

do_compile() {
    oe_runmake -C ${S} all
}
do_compile[cleandirs] = "${B}"

do_install() {
    install -d ${D}${includedir}/optee/export-user_ta/
    for f in ${B}/export-ta_${OPTEE_ARCH}/* ; do
        cp -aR $f ${D}${includedir}/optee/export-user_ta/
    done
}

FILES:${PN} = "${includedir}/optee/"
INSANE_SKIP:${PN}-dev = "staticdev buildpaths"

PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
