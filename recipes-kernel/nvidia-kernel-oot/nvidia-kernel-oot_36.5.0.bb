TEGRA_SRC_SUBARCHIVE = "\
    Linux_for_Tegra/source/kernel_oot_modules_src.tbz2 \
    Linux_for_Tegra/source/nvidia_kernel_display_driver_source.tbz2 \
"
TEGRA_SRC_SUBARCHIVE_OPTS = "-C ${S}"
do_unpack[dirs] =+ "${S}"

require recipes-bsp/tegra-sources/tegra-sources-36.5.0.inc
require nvidia-kernel-oot.inc
