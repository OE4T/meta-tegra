EXTRA_OEMESON:append:tegra = " -Dglx=false"
DEPENDS:append:tegra = " egl-wayland libxshmfence mesa"
PACKAGE_ARCH:tegra = "${TEGRA_PKGARCH}"

RDEPENDS:${PN}:append:tegra = " egl-wayland egl-x11 xkbcomp"
