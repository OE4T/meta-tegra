DESCRIPTION = "NVIDIA OP-TEE fTPM trusted application"
HOMEPAGE = "https://developer.nvidia.com/embedded"
LICENSE = "BSD-2-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=5a3925ece0806073ae9ebbb08ff6f11e"

require optee-l4t.inc
TEGRA_SRC_SUBARCHIVE_OPTS = "--strip-components=1 optee/optee_ftpm"
TEGRA_SRC_SUBARCHIVE_OPTS += "--strip-components=1 optee/samples"
TEGRA_SRC_SUBARCHIVE_OPTS += "--strip-components=1 optee/optee_os"

DEPENDS += "optee-os-tadevkit optee-client"
DEPENDS:append:libc-musl = " argp-standalone"

LDADD:append:libc-musl = " -L${STAGING_LIBDIR} -largp"

export LDADD

S = "${UNPACKDIR}/optee_ftpm"
B = "${WORKDIR}/build"

EXTRA_OEMAKE += " \
    CROSS_COMPILE=${HOST_PREFIX} CFG_MS_TPM_20_REF=${UNPACKDIR}/samples/ms-tpm-20-ref \
    OPTEE_OS_DIR=${UNPACKDIR}/optee_os \
    CFG_TA_MEASURED_BOOT=y \
    CFG_USE_PLATFORM_EPS=y \
" 

do_compile() {
    oe_runmake -C ${S} all
}
do_compile[cleandirs] = "${B}"

do_install() {
    install -d ${D}${nonarch_base_libdir}/optee_armtz
    install -m 0644 ${B}/bc50d971-d4c9-42c4-82cb-343fb7f37896.ta ${D}${nonarch_base_libdir}/optee_armtz

    install -D -m 0755 ${B}/bc50d971-d4c9-42c4-82cb-343fb7f37896.stripped.elf -t ${D}${includedir}/optee/early_ta/optee_ftpm
}

FILES:${PN} += "${nonarch_base_libdir}/optee_armtz/bc50d971-d4c9-42c4-82cb-343fb7f37896.ta"
INSANE_SKIP:${PN} = "already-stripped"
