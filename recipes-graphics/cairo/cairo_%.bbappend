PACKAGECONFIG:append:tegra = " egl glesv2"
PACKAGECONFIG:remove:tegra = "opengl"
PACKAGE_ARCH:tegra = "${TEGRA_PKGARCH}"
