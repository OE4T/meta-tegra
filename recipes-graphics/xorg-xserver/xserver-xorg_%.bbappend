OPENGL_PKGCONFIGS:remove:tegra = "glamor"
PACKAGECONFIG:remove:tegra = "dri2"
PACKAGECONFIG:append:tegra = " xinerama"

PACKAGE_ARCH:tegra = "${TEGRA_PKGARCH}"
