OPENGL_PKGCONFIGS_tegra210 = "dri glx xinerama"
OPENGL_PKGCONFIGS_tegra186 = "dri glx xinerama"
do_install_append_tegra210() {
    rm -rf ${D}${libdir}/xorg/modules/extensions
}
do_install_append_tegra186() {
    rm -rf ${D}${libdir}/xorg/modules/extensions
}

PACKAGE_ARCH_tegra = "${SOC_FAMILY_PKGARCH}"
