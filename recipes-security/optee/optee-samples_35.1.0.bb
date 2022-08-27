SUMMARY = "OP-TEE Samples"
DESCRIPTION = "Open Portable Trusted Execution Environment - Sample applications"
HOMEPAGE = "https://www.op-tee.org/"

LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6938d70d5e5d49d31049419e85bb82f8"

inherit python3native
require optee-tegra.inc

DEPENDS = "python3-cryptography-native optee-os-tadevkit-tegra optee-client-tegra"

S = "${WORKDIR}/optee/samples"
B = "${WORKDIR}/build"

EXTRA_OEMAKE += " \
    CFLAGS32='--sysroot=${STAGING_DIR_HOST}' \
    CFLAGS64='--sysroot=${STAGING_DIR_HOST}' \
    CROSS_COMPILE='${HOST_PREFIX}' \
    PYTHON3='${PYTHON}' \
    TA_DEV_KIT_DIR='${TA_DEV_KIT_DIR}' \
    OPTEE_CLIENT_EXPORT="${D}/usr" \
    O='${B}' \
"

do_compile() {
    oe_runmake -C ${S} all
}
do_compile[cleandirs] = "${B}"

do_install() {
    install -d ${D}${sbindir}
    install -m 0755 ${B}/ca/hwkey-agent/nvhwkey-app ${D}${sbindir}
    install -m 0755 ${B}/ca/luks-srv/nvluks-srv-app ${D}${sbindir}

    install -d ${D}${nonarch_base_libdir}/optee_armtz
    install -m 0644 ${B}/ta/hwkey-agent/82154947-c1bc-4bdf-b89d-04f93c0ea97c.ta ${D}${nonarch_base_libdir}/optee_armtz
    install -m 0644 ${B}/early_ta/luks-srv/b83d14a8-7128-49df-9624-35f14f65ca6c.ta ${D}${nonarch_base_libdir}/optee_armtz

    install -d ${D}${includedir}/optee/early_ta/luks-srv
    install -m 0755 ${B}/early_ta/luks-srv/b83d14a8-7128-49df-9624-35f14f65ca6c.stripped.elf ${D}${includedir}/optee/early_ta/luks-srv
}

FILES:${PN} += "${nonarch_base_libdir}/optee_armtz"
FILES:${PN}-dev = "${includedir}/optee/"
INSANE_SKIP:${PN} = "ldflags already-stripped"

PACKAGE_ARCH = "${SOC_FAMILY_PKGARCH}"
