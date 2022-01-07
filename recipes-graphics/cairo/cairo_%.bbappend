PACKAGECONFIG:append:tegra = " ${@bb.utils.contains('DISTRO_FEATURES', 'opengl', 'egl glesv2', '', d)}"
PACKAGECONFIG:remove:tegra = "opengl"
PACKAGE_ARCH:tegra = "${TEGRA_PKGARCH}"
