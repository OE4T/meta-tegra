USERADD_PACKAGES:append:tegra = " ${PN}-manager-cfg"
GROUPADD_PARAM:${PN}-manager-cfg:tegra = "--system gpio"

PACKAGE_ARCH:tegra = "${TEGRA_PKGARCH}"
