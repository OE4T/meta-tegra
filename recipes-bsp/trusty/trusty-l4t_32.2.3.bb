TEGRA_SRC_SUBARCHIVE = "public_sources/trusty_src.tbz2"
require recipes-bsp/tegra-sources/tegra-sources-32.2.3.inc
S = "${WORKDIR}/trusty"

require trusty-l4t.inc
