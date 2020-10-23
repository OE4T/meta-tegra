TEGRA_SRC_SUBARCHIVE = "Linux_for_Tegra/source/public/atf_src.tbz2"
require recipes-bsp/tegra-sources/tegra-sources-32.4.4.inc
S = "${WORKDIR}/arm-trusted-firmware"

require arm-trusted-firmware.inc
