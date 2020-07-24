FILESEXTRAPATHS_prepend := "${THISDIR}/${BPN}:"
SRC_URI_append_tegra = " file://cksum.cfg"

PACKAGE_ARCH_tegra = "${TEGRA_PKGARCH}"
