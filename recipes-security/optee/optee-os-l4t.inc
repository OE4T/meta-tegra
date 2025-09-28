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
    CFG_CORE_TPM_EVENT_LOG=y CFG_REE_STATE=y CFG_JETSON_FTPM_HELPER_PTA=y \
"

do_compile() {
    oe_runmake -C ${S} all
}
do_compile[cleandirs] = "${B}"

INHIBIT_PACKAGE_STRIP = "1"
INHIBIT_SYSROOT_STRIP = "1"
