TEGRA_SRC_SUBARCHIVE = "Linux_for_Tegra/source/public/trusty_src.tbz2"
require recipes-bsp/tegra-sources/tegra-sources-32.3.1.inc
S = "${WORKDIR}/trusty"

require trusty-l4t.inc
