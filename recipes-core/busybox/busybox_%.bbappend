FILESEXTRAPATHS:prepend := "${THISDIR}/${BPN}:"
SRC_URI:append:tegra = " file://cksum.cfg"

PACKAGE_ARCH:tegra = "${TEGRA_PKGARCH}"
