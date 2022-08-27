SUMMARY = "OP-TEE Trusted OS"
DESCRIPTION = "Open Portable Trusted Execution Environment - Trusted side of the TEE"
HOMEPAGE = "https://www.op-tee.org/"

LICENSE = "BSD-2-Clause & Proprietary"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=2f6a2cb48b5cc5cd0bd3f87a836cb407 \
    file://LICENSE.NVIDIA;md5=ba16bc74328d76e24af960ba01c937dc \
"

inherit python3native
require optee-tegra.inc

DEPENDS = "python3-pyelftools-native python3-cryptography-native optee-samples"

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
    EARLY_TA_PATHS='${STAGING_INCDIR}/optee/early_ta/luks-srv/b83d14a8-7128-49df-9624-35f14f65ca6c.stripped.elf' \
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
    install -d ${D}${datadir}/trusted-os
    install -m 0644 ${B}/core/*.bin ${B}/core/tee.elf ${D}${datadir}/trusted-os/
}

FILES:${PN}-dev = "${datadir}/trusted-os"
# note: "textrel" is not triggered on all archs
INSANE_SKIP:${PN}-dev = "textrel"
# Build paths are currently embedded
INSANE_SKIP:${PN}-dev += "buildpaths"

PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
