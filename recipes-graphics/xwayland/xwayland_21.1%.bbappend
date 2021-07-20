EXTRA_OEMESON_append_tegra = " -Dglx=false -Dxwayland_eglstream=true"
DEPENDS_append_tegra = " egl-wayland libxshmfence mesa"
PACKAGE_ARCH_tegra = "${TEGRA_PKGARCH}"

RDEPENDS_${PN}_append_tegra = " egl-wayland xkbcomp"
