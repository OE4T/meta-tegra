FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI_append_tegra = " file://0001-Make-psplash-fb-compatible-with-tegrafb-driver.patch"
PACKAGE_ARCH_tegra = "${TEGRA_PKGARCH}"
