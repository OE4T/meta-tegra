TEGRA_SRC_SUBARCHIVE = "public_sources/atf_src.tbz2"
require recipes-bsp/tegra-sources/tegra-sources-32.2.3.inc
S = "${WORKDIR}/arm-trusted-firmware"

require arm-trusted-firmware.inc
