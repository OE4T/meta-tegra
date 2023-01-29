PACKAGECONFIG:append:tegra = " glvnd"
PROVIDES:tegra = "virtual/mesa virtual/libgbm"
RDEPENDS:libgbm:append:tegra = " tegra-gbm-backend egl-gbm"
PACKAGE_ARCH:tegra = "${TEGRA_PKGARCH}"
