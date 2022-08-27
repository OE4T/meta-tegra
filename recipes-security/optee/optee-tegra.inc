COMPATIBLE_MACHINE = "(tegra194|tegra234)"

TEGRA_SRC_SUBARCHIVE = "Linux_for_Tegra/source/public/nvidia-jetson-optee-source.tbz2"
require recipes-bsp/tegra-sources/tegra-sources-35.1.0.inc

OPTEE_ARCH = "UNKNOWN"
OPTEE_ARCH:aarch64 = "arm64"

OPTEE_NV_PLATFORM = "UNKNOWN"
OPTEE_NV_PLATFORM:tegra194 = "t194"
OPTEE_NV_PLATFORM:tegra234 = "t234"

NV_CCC_PREBUILT = "${@'${S}/prebuilt/${OPTEE_NV_PLATFORM}/libcommon_crypto.a' if d.getVar('OPTEE_NV_PLATFORM') == 't234' else ''}"
TA_DEV_KIT_DIR = "${STAGING_INCDIR}/optee/export-user_ta"

# python3-cryptography needs the legacy provider, so set OPENSSL_MODULES to the
# right path until this is relocated automatically.
export OPENSSL_MODULES="${STAGING_LIBDIR_NATIVE}/ossl-modules"
