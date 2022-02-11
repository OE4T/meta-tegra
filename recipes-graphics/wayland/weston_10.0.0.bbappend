DEPENDS:append:tegra = " egl-wayland"
PACKAGE_ARCH:tegra = "${TEGRA_PKGARCH}"

RDEPENDS:${PN}:append:tegra = " egl-wayland egl-gbm tegra-udrm-probeconf"
