SUMMARY = "OP-TEE sanity testsuite"
DESCRIPTION = "Open Portable Trusted Execution Environment - Test suite"
HOMEPAGE = "https://www.op-tee.org/"

LICENSE = "BSD-2-Clause & GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=daa2bcccc666345ab8940aab1315a4fa"

require optee-l4t.inc

TEGRA_SRC_SUBARCHIVE_OPTS = "--strip-components=1 optee/optee_test"

DEPENDS += "optee-os-tadevkit optee-client openssl"

S = "${WORKDIR}/optee_test"
B = "${WORKDIR}/build"

EXTRA_OEMAKE += " \
    CROSS_COMPILE_HOST=${HOST_PREFIX} \
    CROSS_COMPILE_TA=${HOST_PREFIX} \
    OPTEE_CLIENT_EXPORT=${STAGING_DIR_HOST}${prefix} \
"

do_compile() {
    # Upstream recipe notes that top-level makefile has parallelism issues
    oe_runmake -C ${S} xtest
    oe_runmake -C ${S} ta
    oe_runmake -C ${S} test_plugin
}
do_compile[cleandirs] = "${B}"

do_install () {
    install -d ${D}${bindir}
    install -m 0755 ${B}/xtest/xtest ${D}${bindir}

    install -d ${D}${nonarch_base_libdir}/optee_armtz/
    install -m 0644 ${B}/ta/*/*.ta ${D}${nonarch_base_libdir}/optee_armtz/
    install -d ${D}${libdir}/tee-supplicant/plugins
    install -m 0644 ${B}/supp_plugin/*.plugin ${D}${libdir}/tee-supplicant/plugins/
}

FILES:${PN} += " \
    ${nonarch_base_libdir}/optee_armtz \
    ${libdir}/tee-supplicant/plugins \
"

RDEPENDS:${PN} = "optee-os optee-client"

CVE_PRODUCT = "linaro:op-tee trustedfirmware:op-tee"

CVE_STATUS[CVE-2021-36133] = "not-applicable-platform: applies to the NXP i.MX CSU driver, not Tegra"
CVE_STATUS[CVE-2026-40290] = "not-applicable-config: requires CFG_SECURE_PARTITION=y with CFG_CORE_SEL1_SPMC; plat-tegra/conf.mk forces CFG_CORE_SEL2_SPMC=n and no recipe sets SP_PATHS, so CFG_SECURE_PARTITION defaults to n and no SPMC mode is built"
CVE_STATUS[CVE-2026-41515] = "not-applicable-platform: requires the NXP CAAM crypto driver (CFG_NXP_CAAM), not built for Tegra"
