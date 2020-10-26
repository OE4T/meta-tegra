TEGRA_SRC_SUBARCHIVE = "Linux_for_Tegra/source/public/trusty_src.tbz2"
require recipes-bsp/tegra-sources/tegra-sources-32.4.4.inc

SRC_URI += " \
    file://0001-common-make-macros.mk-translate-to-_.patch \
    file://0002-kernel-thread.c-fix-thread_timer_tick-call-signature.patch \
"

S = "${WORKDIR}/trusty"

require trusty-l4t.inc

EXCLUDE_FROM_WORLD = "1"
