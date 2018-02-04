OPENGL_PKGCONFIGS_tegra210 = "dri glx xinerama"
OPENGL_PKGCONFIGS_tegra186 = "dri glx xinerama"
do_install_append_tegra210() {
    rm -rf ${D}${libdir}/xorg/modules/extensions
}
do_install_append_tegra186() {
    rm -rf ${D}${libdir}/xorg/modules/extensions
}

PACKAGE_ARCH_tegra210 = "${SOC_FAMILY_PKGARCH}"
PACKAGE_ARCH_tegra124 = "${SOC_FAMILY_PKGARCH}"
PACKAGE_ARCH_tegra186 = "${SOC_FAMILY_PKGARCH}"
