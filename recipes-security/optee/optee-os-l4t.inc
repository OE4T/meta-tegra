require optee-l4t.inc
TEGRA_SRC_SUBARCHIVE_OPTS = "--strip-components=1 optee/optee_os"

LICENSE = "BSD-2-Clause AND LicenseRef-Proprietary"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=21712c598e942c57c6afd1e8148d2459 \
    file://LICENSE.NVIDIA;md5=5c5fdb4106f8f874e7f96eed38030b85 \
"

SRC_URI:append = " file://0001-core-Fix-GCC-15-build-errors.patch;striplevel=2"

OPTEEMACHINE ?= "invalid"
OPTEEMACHINE:tegra ?= "tegra"

OPTEE_ARCH = "null"
OPTEE_ARCH:aarch64 = "arm64"

NV_CCC_PREBUILT = "${S}/prebuilt/${OPTEE_NV_PLATFORM}/libcommon_crypto.a"

S = "${UNPACKDIR}/optee_os"
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
    ${@d.getVar('FTPM_CFG') if d.getVar('OPTEE_ENABLE_FTPM') == '1' else ''} \
"

do_compile() {
    oe_runmake -C ${S} all
}
do_compile[cleandirs] = "${B}"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"

CVE_PRODUCT = "linaro:op-tee trustedfirmware:op-tee"

CVE_STATUS[CVE-2021-36133] = "not-applicable-platform: applies to the NXP i.MX CSU driver, not Tegra"
CVE_STATUS[CVE-2026-40290] = "not-applicable-config: requires CFG_SECURE_PARTITION=y with S-EL0 SPs (CFG_CORE_SEL1_SPMC); plat-tegra/conf.mk forces CFG_CORE_SEL2_SPMC instead"
CVE_STATUS[CVE-2026-45702] = "not-applicable-config: same CFG_CORE_SEL1_SPMC/CFG_SECURE_PARTITION gate as CVE-2026-40290; Tegra uses CFG_CORE_SEL2_SPMC"
CVE_STATUS[CVE-2026-41514] = "not-applicable-platform: requires the Hisilicon HPRE crypto driver (CFG_HISILICON_ACC_V3), not built for Tegra"
CVE_STATUS[CVE-2026-41515] = "not-applicable-platform: requires the NXP CAAM crypto driver, not built for Tegra"
CVE_STATUS[CVE-2026-41516] = "not-applicable-platform: requires the Hisilicon HPRE crypto driver (CFG_HISILICON_ACC_V3), not built for Tegra"
