COMPATIBLE_MACHINE = "(tegra)"

TEGRA_SRC_SUBARCHIVE = "Linux_for_Tegra/source/nvidia-jetson-optee-source.tbz2"

inherit l4t_bsp python3native
require recipes-bsp/tegra-sources/tegra-sources-${L4T_VERSION}.inc

DEPENDS = "python3-pyelftools-native python3-cryptography-native python3-pycryptodome-native"

# python3-cryptography needs the legacy provider, so set OPENSSL_MODULES to the
# right path until this is relocated automatically.
export OPENSSL_MODULES = "${STAGING_LIBDIR_NATIVE}/ossl-modules"
