PACKAGECONFIG_append_tegra210 = " xinerama"
PACKAGECONFIG_append_tegra124 = " xinerama"
do_install_append_tegra210() {
    rm -f ${D}${libdir}/xorg/modules/extensions/libglx.so
}
do_install_append_tegra124() {
    rm -f ${D}${libdir}/xorg/modules/extensions/libglx.so
}

PACKAGE_ARCH_tegra210 = "${SOC_FAMILY_PKGARCH}"
PACKAGE_ARCH_tegra124 = "${SOC_FAMILY_PKGARCH}"
