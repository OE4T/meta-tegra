require linux-yocto-tegra.inc

KERNEL_FEATURES:append:tegra = " features/tegra/rt-compat.scc"
