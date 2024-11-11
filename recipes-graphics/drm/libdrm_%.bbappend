PACKAGE_ARCH:tegra = "${TEGRA_PKGARCH}"

RRECOMMENDS:${PN}:tegra = "kernel-module-nvidia-drm nvidia-drm-loadconf"
