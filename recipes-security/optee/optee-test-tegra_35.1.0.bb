SUMMARY = "OP-TEE sanity testsuite"
DESCRIPTION = "Open Portable Trusted Execution Environment - Test suite"
HOMEPAGE = "https://www.op-tee.org/"

LICENSE = "BSD-2-Clause & GPL-2.0-only"
LIC_FILES_CHKSUM = "file://LICENSE.md;md5=daa2bcccc666345ab8940aab1315a4fa"

inherit python3native
require optee-tegra.inc

DEPENDS = "python3-cryptography-native optee-os-tadevkit-tegra optee-client-tegra"

S = "${WORKDIR}/optee/optee_test"
B = "${WORKDIR}/build"

EXTRA_OEMAKE += " \
    CFLAGS32='--sysroot=${STAGING_DIR_HOST}' \
    CFLAGS64='--sysroot=${STAGING_DIR_HOST}' \
    CROSS_COMPILE='${HOST_PREFIX}' \
    PYTHON3='${PYTHON}' \
    TA_DEV_KIT_DIR='${TA_DEV_KIT_DIR}' \
    OPTEE_CLIENT_EXPORT='${STAGING_DIR_HOST}/usr' \
    O='${B}' \
"

do_compile() {
    oe_runmake -C ${S} all
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

PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
