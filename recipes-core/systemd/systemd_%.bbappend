PACKAGECONFIG:append:tegra = " efi"
PACKAGE_ARCH:tegra = "${TEGRA_PKGARCH}"
# XXX remove when upstream recipe is fixed
DEPENDS:append:tegra = " python3-pyelftools-native"
# XXX
