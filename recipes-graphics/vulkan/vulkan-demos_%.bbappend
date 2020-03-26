PACKAGE_ARCH_tegra = "${SOC_FAMILY_PKGARCH}"
PACKAGECONFIG_tegra = "xcb"

		   
FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://0002-Centralize-asset-path-handling.patch \
            file://0003-Load-UI-overla-font-from-asset-path.patch \
            "

