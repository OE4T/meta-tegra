EXTRA_OEMESON:append:tegra = " -Dglx=false -Dxwayland_eglstream=true"
DEPENDS:append:tegra = " egl-wayland libxshmfence mesa"
PACKAGE_ARCH:tegra = "${TEGRA_PKGARCH}"

RDEPENDS:${PN}:append:tegra = " egl-wayland xkbcomp"
