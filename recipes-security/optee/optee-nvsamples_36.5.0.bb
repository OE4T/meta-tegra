DESCRIPTION = "NVIDIA OP-TEE sample applications for Jetson platforms"
HOMEPAGE = "https://developer.nvidia.com/embedded"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=6938d70d5e5d49d31049419e85bb82f8"

require optee-l4t.inc
TEGRA_SRC_SUBARCHIVE_OPTS = "--strip-components=1 optee/samples"
TEGRA_SRC_SUBARCHIVE_OPTS += "--strip-components=1 optee/optee_os"

SRC_URI += " file://0001-Update-makefiles-for-OE-builds.patch"

DEPENDS += "optee-os-tadevkit optee-client"
DEPENDS:append:libc-musl = " argp-standalone"

LDADD:append:libc-musl = " -L${STAGING_LIBDIR} -largp"

export LDADD

S = "${UNPACKDIR}/samples"
B = "${WORKDIR}/build"

EXTRA_OEMAKE += " \
    CROSS_COMPILE=${HOST_PREFIX} \
    ${@d.getVar('FTPM_CFG', True) if d.getVar('OPTEE_ENABLE_FTPM') == '1' else ''} \
"

MS_TPM_SRC = "${S}/ms-tpm-20-ref/Samples/ARM32-FirmwareTPM/optee_ta"
OPTEE_OS_S = "${UNPACKDIR}/optee_os"

do_compile() {
    oe_runmake -C ${S} all

    if [ "${OPTEE_ENABLE_FTPM}" = "1" ]; then
        oe_runmake -C "${MS_TPM_SRC}" CFG_TA_MEASURED_BOOT=y CFG_USE_PLATFORM_EPS=y OPTEE_OS_DIR=${OPTEE_OS_S}
    fi
}
do_compile[cleandirs] = "${B}"

do_install() {
    install -d ${D}${nonarch_base_libdir}/optee_armtz
    install -m 0644 ${B}/ta/hwkey-agent/82154947-c1bc-4bdf-b89d-04f93c0ea97c.ta ${D}${nonarch_base_libdir}/optee_armtz
    oe_runmake -C ${S}/hwkey-agent/host install DESTDIR="${D}"

    install -D -m 0755 ${B}/early_ta/luks-srv/b83d14a8-7128-49df-9624-35f14f65ca6c.stripped.elf -t ${D}${includedir}/optee/early_ta/luks-srv
    install -D -m 0755 ${B}/early_ta/cpubl-payload-dec/0e35e2c9-b329-4ad9-a2f5-8ca9bbbd7713.stripped.elf -t ${D}${includedir}/optee/early_ta/cpubl-payload-dec
    oe_runmake -C ${S}/luks-srv/host install DESTDIR="${D}"

    if [ "${OPTEE_ENABLE_FTPM}" = "1" ]; then
        install -D -m 0755 ${B}/early_ta/ftpm-helper/a6a3a74a-77cb-433a-990c-1dfb8a3fbc4c.stripped.elf -t ${D}${includedir}/optee/early_ta/ftpm-helper
        install -D -m 0755 ${B}/early_ta/ms-tpm/bc50d971-d4c9-42c4-82cb-343fb7f37896.stripped.elf -t ${D}${includedir}/optee/early_ta/ms-tpm
        oe_runmake -C ${S}/ftpm-helper/host install DESTDIR="${D}"
    fi
}

PACKAGES =+ "${PN}-luks-srv ${PN}-hwkey-agent ${@d.getVar('PN') + '-ftpm-helper' if d.getVar('OPTEE_ENABLE_FTPM') == '1' else ''}"
FILES:${PN}-hwkey-agent = "${nonarch_base_libdir}/optee_armtz/82154947-c1bc-4bdf-b89d-04f93c0ea97c.ta ${sbindir}/nvhwkey-app"
FILES:${PN}-luks-srv = "${sbindir}/nvluks-srv-app"
FILES:${PN}-ftpm-helper = "${sbindir}/nvftpm-helper-app"
ALLOW_EMPTY:${PN} = "1"
RDEPENDS:${PN} = " \
    ${PN}-luks-srv \
    ${PN}-hwkey-agent \
    ${@ \
        d.getVar('PN') + '-ftpm-helper' \
        + ' kernel-module-tpm-ftpm-tee' \
    if d.getVar('OPTEE_ENABLE_FTPM') == '1' else ''} \
"
RRECOMMENDS:${PN} = " ${@ 'tegra-configs-udev' if d.getVar('OPTEE_ENABLE_FTPM') == '1' else ''}"
INHIBIT_SYSROOT_STRIP = "1"
INSANE_SKIP:${PN} = "already-stripped"
