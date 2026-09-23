require optee-l4t.inc
TEGRA_SRC_SUBARCHIVE_OPTS = "--strip-components=1 optee/optee_os"

LICENSE = "BSD-2-Clause & Proprietary"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=2f6a2cb48b5cc5cd0bd3f87a836cb407 \
    file://LICENSE.NVIDIA;md5=ba16bc74328d76e24af960ba01c937dc \
"

OPTEEMACHINE ?= "invalid"
OPTEEMACHINE:tegra ?= "tegra"

OPTEE_ARCH = "null"
OPTEE_ARCH:aarch64 = "arm64"

NV_CCC_PREBUILT = ""
NV_CCC_PREBUILT:tegra234 = "${S}/prebuilt/${OPTEE_NV_PLATFORM}/libcommon_crypto.a"

S = "${WORKDIR}/optee_os"
B = "${WORKDIR}/build"

CFLAGS[unexport] = "1"
LDFLAGS[unexport] = "1"
CPPFLAGS[unexport] = "1"
AS[unexport] = "1"
LD[unexport] = "1"

do_configure[noexec] = "1"

EXTRA_OEMAKE += "\
    PLATFORM=${OPTEEMACHINE} \
    PLATFORM_FLAVOR=${OPTEE_NV_PLATFORM} \
    CROSS_COMPILE_core=${HOST_PREFIX} \
    CROSS_COMPILE_ta_${OPTEE_ARCH}=${HOST_PREFIX} \
    ${@' NV_CCC_PREBUILT=${NV_CCC_PREBUILT}' if d.getVar('NV_CCC_PREBUILT') else ''} \
"

do_compile() {
    oe_runmake -C ${S} all
}
do_compile[cleandirs] = "${B}"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"

CVE_PRODUCT = "linaro:op-tee trustedfirmware:op-tee"

CVE_STATUS[CVE-2021-36133] = "not-applicable-platform: applies to the NXP i.MX CSU driver, not Tegra"
CVE_STATUS[CVE-2026-40290] = "not-applicable-config: requires CFG_SECURE_PARTITION=y with CFG_CORE_SEL1_SPMC; plat-tegra/conf.mk forces CFG_CORE_SEL2_SPMC=n and no recipe sets SP_PATHS, so CFG_SECURE_PARTITION defaults to n and no SPMC mode is built"
CVE_STATUS[CVE-2026-41515] = "not-applicable-platform: requires the NXP CAAM crypto driver (CFG_NXP_CAAM), not built for Tegra"
