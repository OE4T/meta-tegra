COMPATIBLE_MACHINE = "(tegra)"

TEGRA_SRC_SUBARCHIVE = "Linux_for_Tegra/source/nvidia-jetson-optee-source.tbz2"

inherit l4t_bsp python3native
require recipes-bsp/tegra-sources/tegra-sources-${L4T_VERSION}.inc

CVE_PRODUCT = "linaro:op-tee op-tee:op-tee_os"

DEPENDS = "python3-pyelftools-native python3-cryptography-native"
DEPENDS:append:tegra234 = " edk2-nvidia-standalone-mm"
# python3-cryptography needs the legacy provider, so set OPENSSL_MODULES to the
# right path until this is relocated automatically.
export OPENSSL_MODULES = "${STAGING_LIBDIR_NATIVE}/ossl-modules"

TOOLCHAIN = "gcc"

OPTEE_NV_PLATFORM ?= ""
OPTEE_NV_PLATFORM:tegra234 = "t234"
OPTEE_NV_PLATFORM:tegra264 = "t264"

TA_DEV_KIT_DIR = "${STAGING_INCDIR}/optee/export-user_ta"

TEGRA_OPTEE_TA_SIGN_KEY ?= ""

# Common to all of the builds
EXTRA_OEMAKE = "\
    V=1 \
    O=${B} \
    CFLAGS32='${TOOLCHAIN_OPTIONS} ${DEBUG_PREFIX_MAP}' \
    CFLAGS64='${TOOLCHAIN_OPTIONS} ${DEBUG_PREFIX_MAP}' \
    platform-aflags-generic='${DEBUG_PREFIX_MAP} -pipe' \
    TA_DEV_KIT_DIR=${TA_DEV_KIT_DIR} \
"
EXTRA_OEMAKE:append:tegra234 = "\
    CFG_WITH_STMM_SP=y CFG_STMM_PATH=${@oe.path.relative(d.getVar('S'),d.getVar('B'))}/standalone_mm_optee.bin \
"

do_compile:prepend:tegra234() {
    cp ${STAGING_DATADIR}/edk2-nvidia/standalone_mm_optee.bin ${B}/
}
python __anonymous() {
    key = d.getVar("TEGRA_OPTEE_TA_SIGN_KEY")
    if key and os.path.exists(key):
        d.appendVarFlag("do_compile", "file-checksums", f" {key}:True")
        d.appendVar("EXTRA_OEMAKE", f" TA_SIGN_KEY={key}")
}
