FILESEXTRAPATHS_prepend := "${THISDIR}/${PN}:"

SRC_URI += "file://0001-Remove-version-check-for-gl.patch"

OPENGL_PKGCONFIGS_remove_tegra = "glamor"
PACKAGECONFIG_remove_tegra = "dri2"
PACKAGECONFIG_append_tegra = " xinerama"

PACKAGE_ARCH_tegra = "${SOC_FAMILY_PKGARCH}"
